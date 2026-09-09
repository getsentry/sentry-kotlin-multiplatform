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
                sentryExtension.autoInstall.spm
            )
            project.extensions.add(
                COMMON_MAIN_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.commonMain
            )

            // spm4Kmp consumes its swiftPackageConfig container in its own afterEvaluate, which can
            // run before executeConfiguration's afterEvaluate depending on plugin application order,
            // so the Sentry package must be registered eagerly rather than in afterEvaluate. The
            // nested withId makes this robust to plugin application order: the install only runs
            // once both the spm4Kmp and Kotlin Multiplatform plugins are present.
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

        warnOnLateSpmAutoInstallOptOut(project, sentryExtension.autoInstall)

        maybeLinkCocoaFramework(
            project,
            externalProvider = project.externalCocoaFrameworkProvider(),
            hostIsMac
        )
    }

    /**
     * The Sentry Swift package is registered with spm4Kmp as soon as each Apple target is created
     * (inside the `kotlin { }` block), so an auto-install opt-out configured after that block is
     * read too late to take effect. By afterEvaluate both states are final, so a disabled flag
     * combined with the registration marker means the opt-out was silently ignored — warn instead.
     */
    private fun warnOnLateSpmAutoInstallOptOut(
        project: Project,
        autoInstall: AutoInstallExtension
    ) {
        val spmAutoInstalled = project.extensions.extraProperties.has(SPM_AUTO_INSTALLED_MARKER)
        val spmOptedOut = !autoInstall.enabled.get() || !autoInstall.spm.enabled.get()
        if (spmAutoInstalled && spmOptedOut) {
            project.logger.warn(
                "The Sentry Cocoa Swift package was already registered with spm4Kmp before the " +
                    "auto-install was disabled. Place the sentryKmp { } block before the " +
                    "kotlin { } block for the opt-out to take effect."
            )
        }
    }

    companion object {
        internal val logger by lazy {
            LoggerFactory.getLogger(SentryPlugin::class.java)
        }
    }
}

/**
 * Name of the integration that already provides Sentry Cocoa to every Apple target, or null when
 * the plugin has to resolve and link the framework itself.
 *
 * Sentry Cocoa can reach an Apple target three ways, and exactly one of them has to win:
 * 1. CocoaPods, where `pod("Sentry")` lives in a single project-wide container and therefore covers
 *    either every Apple target or none.
 * 2. spm4Kmp, where a bare "sentryCocoa" config is project-wide as well, but `swiftPackageConfig`
 *    on a target registers "sentryCocoa_<Target>" and covers that one target.
 * 3. Nobody, in which case the plugin resolves the framework from DerivedData (or the configured
 *    `linker { }` paths) and links it itself.
 *
 * Because spm4Kmp is the only one that can cover *some* targets, the decision is split in two: this
 * answers the project-wide question, and [targetsNeedingFallbackLinking] sorts out the remaining
 * per-target spm4Kmp configs. CocoaPods is deliberately not re-checked there — being project-wide,
 * a Sentry pod has already short-circuited linking before that filter ever runs.
 */
internal fun Project.externalCocoaFrameworkProvider(): String? {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME) as? KotlinMultiplatformExtension
    val cocoapodsExtension =
        if (plugins.findPlugin(KotlinCocoapodsPlugin::class.java) != null && kmpExtension != null) {
            (kmpExtension as ExtensionAware).extensions.findByType(CocoapodsExtension::class.java)
        } else {
            null
        }
    if (cocoapodsExtension?.pods?.findByName(SENTRY_POD_NAME) != null) {
        return "CocoaPods"
    }
    return if (isSentryConfiguredViaSpm4Kmp()) "spm4Kmp" else null
}

/**
 * The subset of [targets] the plugin still has to link itself, i.e. everything not covered by a
 * per-target spm4Kmp config. Only reached when no project-wide provider exists — see
 * [externalCocoaFrameworkProvider].
 */
internal fun Project.targetsNeedingFallbackLinking(
    targets: List<KotlinNativeTarget>
): List<KotlinNativeTarget> =
    targets.filterNot { target ->
        isSentryConfiguredViaSpm4Kmp(target.name)
    }

private fun maybeLinkCocoaFramework(
    project: Project,
    externalProvider: String?,
    hostIsMac: Boolean
) {
    if (!hostIsMac) {
        project.logger.info("Host is not macOS - skipping Sentry Cocoa framework linking setup.")
        return
    }

    if (externalProvider != null) {
        project.logger.lifecycle(
            "Sentry Cocoa is provided by $externalProvider - skipping Sentry Cocoa framework linking"
        )
        return
    }

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
        val requestedTargets = getActiveTargets(project, appleTargets, graph)
        val activeTargets = project.targetsNeedingFallbackLinking(requestedTargets)

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

        val spmCoveredTargets =
            requestedTargets.filter { project.isSentryConfiguredViaSpm4Kmp(it.name) }
        if (spmCoveredTargets.isNotEmpty()) {
            project.logger.lifecycle(
                "Sentry Cocoa is provided by spm4Kmp for targets: ${spmCoveredTargets.map { it.name }}"
            )
        }

        project.logger.lifecycle("Set up Sentry Cocoa linking for targets: ${activeTargets.map { it.name }}")

        CocoaFrameworkLinker(
            logger = project.logger,
            pathResolver = FrameworkPathResolver(project),
            binaryLinker = FrameworkLinker(project.logger)
        ).configure(appleTargets = activeTargets)
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
        val sentryPod = cocoapods.pods.findByName(SENTRY_POD_NAME)
        if (sentryPod == null) {
            val cocoaVersion = cocoapodsAutoInstallExtension.sentryCocoaVersion.get()
            cocoapods.pod(SENTRY_POD_NAME) {
                version = cocoaVersion
                linkOnly = true
                extraOpts += listOf("-compiler-option", "-fmodules")
            }
            logger.info("Added the Sentry Cocoa $cocoaVersion pod via CocoaPods auto installation.")
        } else {
            logger.info("Sentry pod already configured. Skipping CocoaPods auto installation.")
        }
    }
}

private const val SENTRY_POD_NAME = "Sentry"
internal const val SENTRY_COCOA_CINTEROP_NAME = "sentryCocoa"
private const val SENTRY_COCOA_GIT_URL = "https://github.com/getsentry/sentry-cocoa.git"

internal fun Project.isSentryConfiguredViaSpm4Kmp(targetName: String? = null): Boolean {
    if (!plugins.hasPlugin(SPM4KMP_PLUGIN_ID)) {
        return false
    }
    val swiftPackageConfigs =
        extensions.findByName(SPM4KMP_SWIFT_PACKAGE_CONFIG_EXTENSION_NAME)
            as? NamedDomainObjectContainer<*> ?: return false
    if (SENTRY_COCOA_CINTEROP_NAME in swiftPackageConfigs.names) {
        return true
    }
    if (targetName == null) {
        return false
    }
    val perTargetName =
        "${SENTRY_COCOA_CINTEROP_NAME}_${targetName.replaceFirstChar { it.uppercase() }}"
    return perTargetName in swiftPackageConfigs.names
}

/**
 * Extra-property marker set when the spm4Kmp auto-install actually registered the Sentry Swift
 * package, used to warn when the auto-install opt-out is configured too late to take effect.
 */
internal const val SPM_AUTO_INSTALLED_MARKER = "io.sentry.kotlin.multiplatform.spmAutoInstalled"

/**
 * True when a [SENTRY_COCOA_CINTEROP_NAME] Swift package config already exists for [targetName] in
 * the spm4Kmp container — either a global (non target-scoped) "sentryCocoa" entry or the
 * "sentryCocoa_<TargetCapitalized>" key that spm4Kmp's `swiftPackageConfig(cinteropName)` creates.
 *
 * The container is the only reliable "already configured" signal at configuration time: spm4Kmp
 * creates the actual Kotlin cinterop only in its own afterEvaluate, and capitalizes its name
 * ("SentryCocoa"), so checking the compilation's cinterops would never match a spm4Kmp-managed
 * config.
 *
 * Only sees configs registered before [targetName] was created — see [installSentryForSpm4Kmp].
 */
private fun Project.hasSentrySwiftPackageConfig(targetName: String): Boolean {
    val swiftPackageConfigs =
        extensions.findByName(SPM4KMP_SWIFT_PACKAGE_CONFIG_EXTENSION_NAME)
            as? NamedDomainObjectContainer<*> ?: return false
    val perTargetName =
        "${SENTRY_COCOA_CINTEROP_NAME}_${targetName.replaceFirstChar { it.uppercase() }}"
    return SENTRY_COCOA_CINTEROP_NAME in swiftPackageConfigs.names ||
        perTargetName in swiftPackageConfigs.names
}

/**
 * Adds the Sentry Cocoa Swift package to every Apple target via the spm4Kmp DSL so consumers don't
 * have to declare it themselves. Re-running is a no-op, and a [SENTRY_COCOA_CINTEROP_NAME] config
 * that already exists when the Apple targets are created is left untouched.
 *
 * A user config registered *inside* the target block (`iosArm64 { swiftPackageConfig(...) }`) is not
 * detected, because this runs on target creation and Gradle invokes that block afterwards. spm4Kmp's
 * `swiftPackageConfig` uses `maybeCreate`, so the user config then lands on the object registered
 * here and sentry-cocoa ends up declared twice in the generated Package.swift, which SwiftPM
 * rejects. Such setups have to opt out via `sentryKmp { autoInstall { spm { enabled = false } } }`
 * placed before the `kotlin { }` block.
 */
internal fun Project.installSentryForSpm4Kmp(
    autoInstall: AutoInstallExtension,
    hostIsMac: Boolean = HostManager.hostIsMac
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

        if (hasSentrySwiftPackageConfig(target.name)) {
            logger.info(
                "Sentry Cocoa Swift package already configured for ${target.name}. " +
                    "Skipping spm4Kmp auto installation."
            )
            return@configureEach
        }

        val cocoaVersion = autoInstall.spm.sentryCocoaVersion.get()
        extensions.extraProperties.set(SPM_AUTO_INSTALLED_MARKER, true)
        target.swiftPackageConfig(cinteropName = SENTRY_COCOA_CINTEROP_NAME) {
            dependency {
                remotePackageVersion(
                    url = URI(SENTRY_COCOA_GIT_URL),
                    version = cocoaVersion,
                    products = {
                        // exportToKotlin defaults to false (link only). The published KMP SDK klib
                        // already contains the Sentry cinterop bindings, so consumers only need the
                        // framework available at link time.
                        add("Sentry")
                    }
                )
            }
        }
        logger.info(
            "Registered the Sentry Cocoa $cocoaVersion Swift package with spm4Kmp for ${target.name}."
        )
    }
}
