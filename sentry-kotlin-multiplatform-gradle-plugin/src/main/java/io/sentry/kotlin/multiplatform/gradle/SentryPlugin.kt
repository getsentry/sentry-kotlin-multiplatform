package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.KotlinCocoapodsPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager
import org.slf4j.LoggerFactory

internal const val SENTRY_EXTENSION_NAME = "sentryKmp"
internal const val LINKER_EXTENSION_NAME = "linker"
internal const val AUTO_INSTALL_EXTENSION_NAME = "autoInstall"
internal const val COCOAPODS_AUTO_INSTALL_EXTENSION_NAME = "cocoapods"
internal const val COMMON_MAIN_AUTO_INSTALL_EXTENSION_NAME = "commonMain"
internal const val KOTLIN_EXTENSION_NAME = "kotlin"

@Suppress("unused")
class SentryPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit =
        with(project) {
            val sentryExtension =
                project.extensions.create(
                    SENTRY_EXTENSION_NAME,
                    SentryExtension::class.java,
                    project
                )
            project.extensions.add(LINKER_EXTENSION_NAME, sentryExtension.linker)
            project.extensions.add(AUTO_INSTALL_EXTENSION_NAME, sentryExtension.autoInstall)
            project.extensions.add(
                COCOAPODS_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.cocoapods
            )
            project.extensions.add(
                COMMON_MAIN_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.commonMain
            )

            afterEvaluate {
                executeConfiguration(project)
            }
        }

    internal fun executeConfiguration(
        project: Project,
        hostIsMac: Boolean = HostManager.hostIsMac
    ) {
        val sentryExtension = project.extensions.getByType(SentryExtension::class.java)
        val hasCocoapodsPlugin =
            project.plugins.findPlugin(KotlinCocoapodsPlugin::class.java) != null

        if (sentryExtension.autoInstall.enabled.get()) {
            val autoInstall = sentryExtension.autoInstall

            if (autoInstall.commonMain.enabled.get()) {
                project.installSentryForKmp(autoInstall.commonMain)
            }

            if (hasCocoapodsPlugin && autoInstall.cocoapods.enabled.get() && hostIsMac) {
                project.installSentryForCocoapods(autoInstall.cocoapods)
            }
        }

        maybeLinkCocoaFramework(project, hasCocoapodsPlugin, hostIsMac)
    }

    companion object {
        internal val logger by lazy {
            LoggerFactory.getLogger(SentryPlugin::class.java)
        }
    }
}

private fun maybeLinkCocoaFramework(
    project: Project,
    hasCocoapods: Boolean,
    hostIsMac: Boolean
) {
    if (hostIsMac && !hasCocoapods) {
        // Register a task graph listener so that we only configure Cocoa framework linking
        // if at least one Apple target task is part of the requested task graph. This avoids
        // executing the (potentially expensive) path-resolution logic when the build is only
        // concerned with non-Apple targets such as Android.

        val kmpExtension =
            project.extensions.findByName(KOTLIN_EXTENSION_NAME) as? KotlinMultiplatformExtension
                ?: throw GradleException("Error fetching Kotlin Multiplatform extension.")

        val appleTargets = kmpExtension.appleTargets().toList()

        if (appleTargets.isEmpty()) {
            project.logger.info("No Apple targets detected – skipping Sentry Cocoa framework linking setup.")
            return
        }

        project.gradle.taskGraph.whenReady { graph ->
            // Check which of the Kotlin/Native targets are actually in the graph
            val activeTargets = getActiveTargets(project, appleTargets, graph)

            if (activeTargets.isEmpty()) {
                project.logger.lifecycle(
                    "No Apple compile task scheduled for this build " +
                        "- skipping Sentry Cocoa framework linking"
                )
                return@whenReady
            }

            project.logger.lifecycle("Set up Sentry Cocoa linking for targets: ${activeTargets.map { it.name }}")

            CocoaFrameworkLinker(
                logger = project.logger,
                pathResolver = FrameworkPathResolver(project),
                binaryLinker = FrameworkLinker(project.logger)
            ).configure(appleTargets = activeTargets)
        }
    }
}

private fun getActiveTargets(
    project: Project,
    appleTargets: List<KotlinNativeTarget>,
    graph: TaskExecutionGraph
): List<KotlinNativeTarget> = appleTargets.filter { target ->
    val targetName = target.name.replaceFirstChar {
        it.uppercase()
    }
    val path = if (project.path == ":") {
        ":compileKotlin$targetName"
    } else {
        "${project.path}:compileKotlin$targetName"
    }
    try {
        graph.hasTask(path)
    } catch (_: Exception) {
        false
    }
}

internal fun Project.installSentryForKmp(
    commonMainAutoInstallExtension: SourceSetAutoInstallExtension
) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension) {
        logger.info("Kotlin Multiplatform plugin not found. Skipping Sentry installation.")
        return
    }

    val unsupportedTargets = listOf("androidNative")
    kmpExtension.targets.forEach { target ->
        if (unsupportedTargets.any { unsupported -> target.name.contains(unsupported) }) {
            throw GradleException(
                "Unsupported target: ${target.name}. " +
                    "Cannot auto install in commonMain. " +
                    "Please create an intermediate sourceSet with targets that the Sentry SDK " +
                    "supports and add the dependency manually."
            )
        }
    }

    val commonMain = kmpExtension.sourceSets.find { it.name.contains("common") }

    val sentryVersion = commonMainAutoInstallExtension.sentryKmpVersion.get()
    commonMain?.dependencies { api("io.sentry:sentry-kotlin-multiplatform:$sentryVersion") }
}

internal fun Project.installSentryForCocoapods(
    cocoapodsAutoInstallExtension: CocoapodsAutoInstallExtension
) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension || kmpExtension.targets.isEmpty() || !HostManager.hostIsMac) {
        logger.info("Skipping Cocoapods installation.")
        return
    }

    (kmpExtension as ExtensionAware).extensions.configure(CocoapodsExtension::class.java) { cocoapods
        ->
        val podName = "Sentry"
        val sentryPod = cocoapods.pods.findByName(podName)
        if (sentryPod == null) {
            cocoapods.pod(podName) {
                version = cocoapodsAutoInstallExtension.sentryCocoaVersion.get()
                linkOnly = true
                extraOpts += listOf("-compiler-option", "-fmodules")
            }
        }
    }
}
