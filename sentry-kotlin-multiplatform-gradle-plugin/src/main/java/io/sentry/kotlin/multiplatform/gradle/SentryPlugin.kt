package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.KotlinCocoapodsPlugin
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

    internal fun executeConfiguration(project: Project, hostIsMac: Boolean = HostManager.hostIsMac) {
        val sentryExtension = project.extensions.getByType(SentryExtension::class.java)
        val hasCocoapodsPlugin = project.plugins.findPlugin(KotlinCocoapodsPlugin::class.java) != null

        if (sentryExtension.autoInstall.enabled.get()) {
            val autoInstall = sentryExtension.autoInstall

            if (autoInstall.commonMain.enabled.get()) {
                project.installSentryForKmp(autoInstall.commonMain)
            }

            if (hasCocoapodsPlugin && autoInstall.cocoapods.enabled.get() && hostIsMac) {
                project.installSentryForCocoapods(autoInstall.cocoapods)
            }
        }

        if (hostIsMac && !hasCocoapodsPlugin) {
            project.logger.info("Cocoapods plugin not found. Attempting to link Sentry Cocoa framework.")

            val kmpExtension = project.extensions.findByName(KOTLIN_EXTENSION_NAME) as? KotlinMultiplatformExtension
            val appleTargets = kmpExtension?.appleTargets()?.toList()
                ?: throw GradleException("Error fetching Apple targets from Kotlin Multiplatform plugin.")

            CocoaFrameworkLinker(
                logger = project.logger,
                pathResolver = FrameworkPathResolver(project),
                binaryLinker = FrameworkLinker(project.logger)
            ).configure(appleTargets)
        }
    }

    companion object {
        internal val logger by lazy {
            LoggerFactory.getLogger(SentryPlugin::class.java)
        }
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

    val unsupportedTargets = listOf("wasm", "js", "mingw", "linux", "androidNative")
    kmpExtension.targets.forEach { target ->
        if (unsupportedTargets.any { unsupported -> target.name.contains(unsupported) }) {
            throw GradleException(
                "Unsupported target: ${target.name}. " +
                    "Cannot auto install in commonMain. " +
                    "Please create an intermediate sourceSet with targets that the Sentry SDK " +
                    "supports (apple, jvm, android) and add the dependency manually."
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
