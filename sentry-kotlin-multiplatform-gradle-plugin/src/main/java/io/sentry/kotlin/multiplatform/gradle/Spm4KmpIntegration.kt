package io.sentry.kotlin.multiplatform.gradle

import io.github.frankois944.spmForKmp.swiftPackageConfig
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.net.URI

internal const val SENTRY_COCOA_CINTEROP_NAME = "sentryCocoa"
private const val SENTRY_COCOA_GIT_URL = "https://github.com/getsentry/sentry-cocoa.git"

/** Set once the auto-install has registered the package, so a too-late opt-out is detectable. */
internal const val SPM_AUTO_INSTALLED_MARKER = "io.sentry.kotlin.multiplatform.spmAutoInstalled"

/** Null when spm4Kmp isn't applied, which is not the same as an empty container. */
private fun Project.swiftPackageConfigNames(): Set<String>? {
    if (!plugins.hasPlugin(SPM4KMP_PLUGIN_ID)) {
        return null
    }
    val container =
        extensions.findByName(SPM4KMP_SWIFT_PACKAGE_CONFIG_EXTENSION_NAME)
            as? NamedDomainObjectContainer<*> ?: return null
    return container.names
}

/** The container key spm4Kmp's `swiftPackageConfig(cinteropName)` creates for [targetName]. */
private fun spm4KmpConfigName(targetName: String): String {
    val capitalizedTarget = targetName.replaceFirstChar { it.uppercase() }
    return "${SENTRY_COCOA_CINTEROP_NAME}_$capitalizedTarget"
}

private fun Project.sentrySwiftPackageConfigNames(): Set<String> =
    swiftPackageConfigNames()
        .orEmpty()
        .filterTo(mutableSetOf()) {
            it == SENTRY_COCOA_CINTEROP_NAME || it.startsWith("${SENTRY_COCOA_CINTEROP_NAME}_")
        }

/**
 * True when spm4Kmp already supplies Sentry Cocoa to [targetName].
 *
 * A bare "sentryCocoa" entry (spm4Kmp's legacy style) looks project-wide but isn't: spm4Kmp
 * connects it to targets through matching cinterop tasks, so it only reaches the targets that
 * declare the cinterop themselves.
 */
internal fun Project.isSentryConfiguredViaSpm4Kmp(targetName: String): Boolean {
    val configNames = swiftPackageConfigNames() ?: return false
    if (spm4KmpConfigName(targetName) in configNames) {
        return true
    }
    return SENTRY_COCOA_CINTEROP_NAME in configNames && declaresSentryCinterop(targetName)
}

private fun Project.declaresSentryCinterop(targetName: String): Boolean {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME) as? KotlinMultiplatformExtension
    val target = kmpExtension?.targets?.findByName(targetName) as? KotlinNativeTarget ?: return false
    val mainCompilation = target.compilations.findByName("main") as? KotlinNativeCompilation
    return mainCompilation?.cinterops?.findByName(SENTRY_COCOA_CINTEROP_NAME) != null
}

/**
 * Adds the Sentry Cocoa Swift package to every Apple target via the spm4Kmp DSL so consumers don't
 * have to declare it themselves.
 *
 * A single config the consumer wrote suppresses the auto-install for *all* targets, not just the
 * one it names: spm4Kmp merges configs sharing a cinterop name into one SwiftPM package, so a
 * second one would put two sentry-cocoa versions in that package and silently pick one.
 *
 * On the eager path (spm4Kmp applied first) this runs as each target is created, so it cannot see
 * a config declared *inside* a target block; those setups have to opt out before `kotlin { }`.
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

    // Ours, so anything else in the container is recognisable as the consumer's.
    val autoInstalledConfigNames = mutableSetOf<String>()

    kmpExtension.appleTargets().configureEach { target ->
        if (target.konanTarget == KonanTarget.WATCHOS_ARM32) {
            logger.warn(
                "Sentry KMP uses a no-op SDK for watchosArm32 (${target.name}); " +
                    "errors, crashes and logs are not reported. Skipping Sentry Cocoa installation.",
            )
            return@configureEach
        }

        if (!autoInstall.enabled.get() || !autoInstall.spm.enabled.get()) {
            return@configureEach
        }

        val userDefinedConfigNames = sentrySwiftPackageConfigNames() - autoInstalledConfigNames
        if (userDefinedConfigNames.isNotEmpty()) {
            if (!isSentryConfiguredViaSpm4Kmp(target.name)) {
                logger.warn(
                    "Sentry Cocoa is configured with spm4Kmp in $userDefinedConfigNames, so the " +
                        "auto-install left ${target.name} alone rather than add a second " +
                        "sentry-cocoa package that would override that configuration. Add " +
                        "${target.name} to your own swiftPackageConfig if it needs Sentry Cocoa.",
                )
            } else {
                logger.info(
                    "Sentry Cocoa Swift package already configured for ${target.name}. " +
                        "Skipping spm4Kmp auto installation.",
                )
            }
            return@configureEach
        }

        registerSentrySwiftPackage(target, autoInstall.spm.sentryCocoaVersion.get())
        autoInstalledConfigNames += spm4KmpConfigName(target.name)
    }
}

private fun Project.registerSentrySwiftPackage(
    target: KotlinNativeTarget,
    cocoaVersion: String,
) {
    extensions.extraProperties.set(SPM_AUTO_INSTALLED_MARKER, true)
    target.swiftPackageConfig(cinteropName = SENTRY_COCOA_CINTEROP_NAME) {
        // spm4Kmp selects one entry for the shared container, so every entry needs all four
        // minimums, including platforms other than this target's own family.
        minIos = minimumDeploymentVersion(minIos, "15.0")
        minTvos = minimumDeploymentVersion(minTvos, "15.0")
        minMacos = minimumDeploymentVersion(minMacos, "12.0")
        minWatchos = minimumDeploymentVersion(minWatchos, "9.0")
        dependency {
            remotePackageVersion(
                url = URI(SENTRY_COCOA_GIT_URL),
                version = cocoaVersion,
                products = {
                    // Link only (exportToKotlin defaults to false): the published klib already
                    // carries the Sentry cinterop bindings.
                    add("Sentry")
                },
            )
        }
    }
    if (target.konanTarget == KonanTarget.WATCHOS_SIMULATOR_ARM64) {
        registerWatchosSimulatorFrameworkCopy()
    }
    logger.lifecycle(
        "Registered the Sentry Cocoa $cocoaVersion Swift package with spm4Kmp for ${target.name}.",
    )
}

/** Preserve higher consumer minimums, comparing numeric components rather than strings. */
private fun minimumDeploymentVersion(
    configured: String?,
    required: String,
): String {
    val configuredParts = configured?.split('.')?.map { it.toInt() } ?: return required
    val requiredParts = required.split('.').map { it.toInt() }
    for (index in 0 until maxOf(configuredParts.size, requiredParts.size)) {
        val comparison = configuredParts.getOrElse(index) { 0 }.compareTo(requiredParts.getOrElse(index) { 0 })
        if (comparison != 0) {
            return if (comparison > 0) configured else required
        }
    }
    return configured
}
