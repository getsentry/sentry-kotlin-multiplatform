package io.sentry.kotlin.multiplatform.gradle

import io.github.frankois944.spmForKmp.swiftPackageConfig
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
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
import java.net.URI

internal const val SENTRY_EXTENSION_NAME = "sentryKmp"
internal const val LINKER_EXTENSION_NAME = "linker"
internal const val AUTO_INSTALL_EXTENSION_NAME = "autoInstall"
internal const val COCOAPODS_AUTO_INSTALL_EXTENSION_NAME = "cocoapods"
internal const val SPM4KMP_AUTO_INSTALL_EXTENSION_NAME = "spm"
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
                    project
                )
            project.extensions.add(LINKER_EXTENSION_NAME, sentryExtension.linker)
            project.extensions.add(AUTO_INSTALL_EXTENSION_NAME, sentryExtension.autoInstall)
            project.extensions.add(
                COCOAPODS_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.cocoapods
            )
            project.extensions.add(
                SPM4KMP_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.spm,
            )
            project.extensions.add(
                COMMON_MAIN_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.commonMain
            )

            // spm4Kmp consumes its swiftPackageConfig during the configuration phase (before
            // afterEvaluate), so the Sentry package must be registered as soon as the spm4Kmp plugin
            // is applied rather than in executeConfiguration's afterEvaluate. The nested withId makes
            // this robust to plugin application order: the install only runs once both the spm4Kmp
            // and Kotlin Multiplatform plugins are present.
            project.plugins.withId(SPM4KMP_PLUGIN_ID) {
                project.plugins.withId(KOTLIN_MULTIPLATFORM_PLUGIN_ID) {
                    project.installSentryForSpm4Kmp(sentryExtension.autoInstall)
                }
            }

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

            // The spm4Kmp install is wired in apply() via plugins.withId, which fires regardless of
            // plugin application order, so it is intentionally not invoked here.
        }

        // When CocoaPods or spm4Kmp provide the Sentry framework, they also handle linking, so the
        // manual DerivedData-based linker is only needed as a fallback for plain SPM users. Merely
        // applying the spm4Kmp plugin (e.g. for other Swift packages, with the Sentry spm
        // auto-install opted out) must not disable that fallback, so the spm4Kmp check requires the
        // Sentry Swift package to actually be configured.
        maybeLinkCocoaFramework(
            project,
            frameworkProvidedExternally = hasCocoapodsPlugin || project.isSentryConfiguredViaSpm4Kmp(),
            hostIsMac
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
    frameworkProvidedExternally: Boolean,
    hostIsMac: Boolean
) {
    if (hostIsMac && !frameworkProvidedExternally) {
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

    (kmpExtension as ExtensionAware).extensions.configure(CocoapodsExtension::class.java) { cocoapods ->
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

internal const val SENTRY_COCOA_CINTEROP_NAME = "sentryCocoa"
private const val SENTRY_COCOA_GIT_URL = "https://github.com/getsentry/sentry-cocoa.git"

/**
 * True when the Sentry Cocoa Swift package is registered with spm4Kmp — either through the
 * auto-install or a user-defined [SENTRY_COCOA_CINTEROP_NAME] config. spm4Kmp keys per-target
 * entries as "<cinteropName>_<TargetCapitalized>" in its swiftPackageConfig container.
 *
 * Only Gradle core types are used here on purpose: spm4Kmp is a compileOnly dependency, so its
 * classes must not be touched unless the consumer actually applies the spm4Kmp plugin.
 */
internal fun Project.isSentryConfiguredViaSpm4Kmp(): Boolean {
    if (!plugins.hasPlugin(SPM4KMP_PLUGIN_ID)) {
        return false
    }
    val swiftPackageConfigs =
        extensions.findByName(SPM4KMP_SWIFT_PACKAGE_CONFIG_EXTENSION_NAME)
            as? NamedDomainObjectContainer<*> ?: return false
    return swiftPackageConfigs.names.any { name ->
        name == SENTRY_COCOA_CINTEROP_NAME || name.startsWith("${SENTRY_COCOA_CINTEROP_NAME}_")
    }
}

/**
 * Adds the Sentry Cocoa Swift package to every Apple target via the spm4Kmp DSL so consumers don't
 * have to declare it themselves. Idempotent: skips any target that already has a [SENTRY_COCOA_CINTEROP_NAME]
 * cinterop (e.g. a user-defined Sentry config) and re-running is a no-op.
 */
internal fun Project.installSentryForSpm4Kmp(
    autoInstall: AutoInstallExtension,
    hostIsMac: Boolean = HostManager.hostIsMac,
) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension || !hostIsMac) {
        logger.info("Skipping spm4Kmp installation.")
        return
    }

    kmpExtension.appleTargets().configureEach { target ->
        if (!autoInstall.enabled.get() || !autoInstall.spm.enabled.get()) {
            return@configureEach
        }

        val mainCompilation = target.compilations.findByName("main")
        if (mainCompilation?.cinterops?.findByName(SENTRY_COCOA_CINTEROP_NAME) != null) {
            logger.info(
                "Sentry Cocoa Swift package already configured for ${target.name}. " +
                    "Skipping spm4Kmp auto installation.",
            )
            return@configureEach
        }

        target.swiftPackageConfig(cinteropName = SENTRY_COCOA_CINTEROP_NAME) {
            dependency {
                remotePackageVersion(
                    url = URI(SENTRY_COCOA_GIT_URL),
                    version = autoInstall.spm.sentryCocoaVersion.get(),
                    products = {
                        // exportToKotlin defaults to false (link only). The published KMP SDK klib
                        // already contains the Sentry cinterop bindings, so consumers only need the
                        // framework available at link time.
                        add("Sentry")
                    },
                )
            }
        }
    }
}
