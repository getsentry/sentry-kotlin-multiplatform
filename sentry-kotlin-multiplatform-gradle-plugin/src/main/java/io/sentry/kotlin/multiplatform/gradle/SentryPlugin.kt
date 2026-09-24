package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.execution.TaskExecutionGraph
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager
import org.slf4j.LoggerFactory

internal const val SENTRY_EXTENSION_NAME = "sentryKmp"
internal const val LINKER_EXTENSION_NAME = "linker"
internal const val AUTO_INSTALL_EXTENSION_NAME = "autoInstall"
internal const val SPM4KMP_AUTO_INSTALL_EXTENSION_NAME = "spm"
internal const val COMMON_MAIN_AUTO_INSTALL_EXTENSION_NAME = "commonMain"
internal const val KOTLIN_EXTENSION_NAME = "kotlin"
internal const val SPM4KMP_PLUGIN_ID = "io.github.frankois944.spmForKmp"
internal const val SPM4KMP_SWIFT_PACKAGE_CONFIG_EXTENSION_NAME = "swiftPackageConfig"

@Suppress("unused")
class SentryPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit =
        with(project) {
            val sentryExtension =
                project.extensions.create(
                    SENTRY_EXTENSION_NAME,
                    SentryExtension::class.java,
                    project,
                )
            project.extensions.add(LINKER_EXTENSION_NAME, sentryExtension.linker)
            project.extensions.add(AUTO_INSTALL_EXTENSION_NAME, sentryExtension.autoInstall)
            project.extensions.add(
                SPM4KMP_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.spm,
            )
            project.extensions.add(
                COMMON_MAIN_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.commonMain,
            )

            val spmAppliedFirst = plugins.hasPlugin(SPM4KMP_PLUGIN_ID)
            // Register before spm4Kmp's afterEvaluate callback, after user configuration is complete.
            afterEvaluate {
                executeConfiguration(project, spmAppliedFirst = spmAppliedFirst)
            }
        }

    internal fun executeConfiguration(
        project: Project,
        hostIsMac: Boolean = HostManager.hostIsMac,
        spmAppliedFirst: Boolean = false,
    ) {
        val sentryExtension = project.extensions.getByType(SentryExtension::class.java)

        if (sentryExtension.autoInstall.enabled.get()) {
            val autoInstall = sentryExtension.autoInstall

            if (autoInstall.commonMain.enabled.get()) {
                project.installSentryForKmp(autoInstall.commonMain)
            }

            val useSpmAutoInstall = project.plugins.hasPlugin(SPM4KMP_PLUGIN_ID) && autoInstall.spm.enabled.get()
            if (useSpmAutoInstall) {
                if (spmAppliedFirst) {
                    throw GradleException(
                        "Sentry Cocoa auto-install requires the Sentry plugin to be applied before spm4Kmp. " +
                            "Move the Sentry plugin before spm4Kmp in your plugins block.",
                    )
                }
                project.installSentryForSpm4Kmp(autoInstall, hostIsMac)
            }
        }

        maybeLinkCocoaFramework(
            project,
            hostIsMac,
        )
    }

    companion object {
        internal val logger by lazy {
            LoggerFactory.getLogger(SentryPlugin::class.java)
        }
    }
}

private fun maybeLinkCocoaFramework(
    project: Project,
    hostIsMac: Boolean,
) {
    if (!hostIsMac) {
        project.logger.info("Host is not macOS - skipping Sentry Cocoa framework linking setup.")
        return
    }

    val kmpExtension =
        project.extensions.findByName(KOTLIN_EXTENSION_NAME) as? KotlinMultiplatformExtension
            ?: throw GradleException("Error fetching Kotlin Multiplatform extension.")

    val appleTargets = kmpExtension.appleTargets().toList()

    if (appleTargets.isEmpty()) {
        project.logger.info("No Apple targets detected – skipping Sentry Cocoa framework linking setup.")
        return
    }

    project.gradle.taskGraph.whenReady { graph ->
        val requestedTargets = getActiveTargets(project, appleTargets, graph)
        val (spmCoveredTargets, activeTargets) =
            requestedTargets.partition { project.isSentryConfiguredViaSpm4Kmp(it.name) }

        if (activeTargets.isEmpty()) {
            val message =
                if (requestedTargets.isEmpty()) {
                    "No Apple compile task scheduled for this build"
                } else {
                    "Sentry Cocoa is provided by spm4Kmp for all requested Apple targets"
                }
            project.logger.lifecycle("$message - skipping Sentry Cocoa framework linking")
            return@whenReady
        }

        if (spmCoveredTargets.isNotEmpty()) {
            project.logger.lifecycle(
                "Sentry Cocoa is provided by spm4Kmp for targets: ${spmCoveredTargets.map { it.name }}",
            )
        }

        project.logger.lifecycle("Set up Sentry Cocoa linking for targets: ${activeTargets.map { it.name }}")

        CocoaFrameworkLinker(
            logger = project.logger,
            pathResolver = FrameworkPathResolver(project),
            binaryLinker = FrameworkLinker(project.logger),
        ).configure(appleTargets = activeTargets)
    }
}

private fun getActiveTargets(
    project: Project,
    appleTargets: List<KotlinNativeTarget>,
    graph: TaskExecutionGraph,
): List<KotlinNativeTarget> =
    appleTargets.filter { target ->
        val targetName =
            target.name.replaceFirstChar {
                it.uppercase()
            }
        val path =
            if (project.path == ":") {
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

internal fun Project.installSentryForKmp(commonMainAutoInstallExtension: SourceSetAutoInstallExtension) {
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
                    "supports and add the dependency manually.",
            )
        }
    }

    val commonMain = kmpExtension.sourceSets.find { it.name.contains("common") }

    val sentryVersion = commonMainAutoInstallExtension.sentryKmpVersion.get()
    commonMain?.dependencies { api("io.sentry:sentry-kotlin-multiplatform:$sentryVersion") }
}
