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
internal const val COMMON_MAIN_AUTO_INSTALL_EXTENSION_NAME = "commonMain"
internal const val KOTLIN_EXTENSION_NAME = "kotlin"
internal const val KOTLIN_MULTIPLATFORM_PLUGIN_ID = "org.jetbrains.kotlin.multiplatform"
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
        val autoInstall = sentryExtension.autoInstall
        var swiftPmCoveredTargets = emptySet<String>()

        if (autoInstall.enabled.get()) {
            if (autoInstall.commonMain.enabled.get()) {
                project.installSentryForKmp(autoInstall.commonMain)
            }

            val provider = project.resolveAppleDependencyProvider(autoInstall)
            project.warnOnConflictingAppleDependencies(provider)
            swiftPmCoveredTargets = project.installAppleDependency(provider, autoInstall, hostIsMac, spmAppliedFirst)
        }

        maybeLinkCocoaFramework(
            project,
            sentryExtension.linker,
            swiftPmCoveredTargets,
            hostIsMac,
        )
    }

    companion object {
        internal val logger by lazy {
            LoggerFactory.getLogger(SentryPlugin::class.java)
        }
    }
}

/** Returns the names of targets for which official SwiftPM now supplies Sentry Cocoa. */
private fun Project.installAppleDependency(
    provider: AppleDependencyProvider,
    autoInstall: AutoInstallExtension,
    hostIsMac: Boolean,
    spmAppliedFirst: Boolean,
): Set<String> {
    val cocoaVersion = autoInstall.apple.sentryCocoaVersion.get()
    when (provider) {
        AppleDependencyProvider.SWIFT_PM -> {
            if (hostIsMac) {
                return officialSwiftPmExtension()
                    ?.let { OfficialSwiftPmIntegration.install(this, it, cocoaVersion) }
                    .orEmpty()
            }
        }
        AppleDependencyProvider.SPM4KMP -> {
            if (spmAppliedFirst) {
                throw GradleException(
                    "Sentry Cocoa auto-install requires the Sentry plugin to be applied before spm4Kmp. " +
                        "Move the Sentry plugin before spm4Kmp in your plugins block. If you use spm4Kmp only " +
                        "for other packages, set sentryKmp.autoInstall.apple.provider to SWIFT_PM (Kotlin 2.4+) " +
                        "or NONE instead.",
                )
            }
            installSentryForSpm4Kmp(cocoaVersion, hostIsMac)
        }
        AppleDependencyProvider.AUTO, AppleDependencyProvider.NONE ->
            logger.info(
                "Sentry Cocoa is not installed automatically because " +
                    "${noAppleDependencyReason(autoInstall.apple.provider.get())} Add Sentry Cocoa to your app " +
                    "yourself (for example with Swift Package Manager in Xcode); the plugin still links it.",
            )
    }
    return emptySet()
}

private fun maybeLinkCocoaFramework(
    project: Project,
    linker: LinkerExtension,
    swiftPmCoveredTargets: Set<String>,
    hostIsMac: Boolean,
) {
    if (!hostIsMac) {
        project.logger.info("Host is not macOS - skipping Sentry Cocoa framework linking setup.")
        return
    }

    if (!linker.enabled.get()) {
        project.logger.lifecycle("sentryKmp.linker.enabled is false - skipping Sentry Cocoa framework linking")
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
            requestedTargets.partition {
                it.name in swiftPmCoveredTargets || project.isSentryConfiguredViaSpm4Kmp(it.name)
            }

        if (activeTargets.isEmpty()) {
            val message =
                if (requestedTargets.isEmpty()) {
                    "No Apple compile task scheduled for this build"
                } else {
                    "Sentry Cocoa is provided by SwiftPM for all requested Apple targets"
                }
            project.logger.lifecycle("$message - skipping Sentry Cocoa framework linking")
            return@whenReady
        }

        if (spmCoveredTargets.isNotEmpty()) {
            project.logger.lifecycle(
                "Sentry Cocoa is provided by SwiftPM for targets: ${spmCoveredTargets.map { it.name }}",
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
