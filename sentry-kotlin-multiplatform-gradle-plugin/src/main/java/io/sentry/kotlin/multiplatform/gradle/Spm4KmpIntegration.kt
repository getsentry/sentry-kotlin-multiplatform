package io.sentry.kotlin.multiplatform.gradle

import io.github.frankois944.spmForKmp.swiftPackageConfig
import io.sentry.BuildConfig
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

private fun Project.swiftPackageConfigNames(): Set<String>? {
    if (!plugins.hasPlugin(SPM4KMP_PLUGIN_ID)) {
        return null
    }
    val container =
        extensions.findByName(SPM4KMP_SWIFT_PACKAGE_CONFIG_EXTENSION_NAME)
            as? NamedDomainObjectContainer<*> ?: return null
    // spm4Kmp capitalizes the first character when generating task names.
    return container.names.mapTo(mutableSetOf()) { name ->
        name.replaceFirstChar { it.lowercase() }
    }
}

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
 * A global "sentryCocoa" config only covers targets with a matching cinterop.
 * Target-specific configs create that cinterop automatically.
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
    // Both spellings produce the cinteropSentryCocoa task name used by spm4Kmp.
    return mainCompilation?.cinterops?.any {
        it.name == SENTRY_COCOA_CINTEROP_NAME || it.name == "SentryCocoa"
    } == true
}

/**
 * Skip auto-install when a user config exists. spm4Kmp selects one configuration per cinterop
 * name, so adding defaults could replace the user's package settings.
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

    if (!autoInstall.enabled.get() || !autoInstall.spm.enabled.get()) {
        return
    }

    val userDefinedConfigNames = sentrySwiftPackageConfigNames()
    val cocoaVersion = autoInstall.spm.sentryCocoaVersion.get()
    warnIfCocoaVersionMismatch(cocoaVersion, kmpExtension.appleTargets(), userDefinedConfigNames)
    kmpExtension.appleTargets().forEach { target ->
        if (target.konanTarget == KonanTarget.WATCHOS_ARM32) {
            logger.warn(
                "Sentry KMP uses a no-op SDK for watchosArm32 (${target.name}); " +
                    "errors, crashes and logs are not reported. Skipping Sentry Cocoa installation.",
            )
            return@forEach
        }

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
            return@forEach
        }

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
}

private fun Project.warnIfCocoaVersionMismatch(
    cocoaVersion: String,
    appleTargets: Collection<KotlinNativeTarget>,
    userDefinedConfigNames: Set<String>,
) {
    if (userDefinedConfigNames.isEmpty() &&
        appleTargets.any { it.konanTarget != KonanTarget.WATCHOS_ARM32 } &&
        cocoaVersion != BuildConfig.SentryCocoaVersion
    ) {
        logger.warn(
            "autoInstall.spm.sentryCocoaVersion is set to $cocoaVersion, but this Sentry KMP " +
                "Gradle plugin expects ${BuildConfig.SentryCocoaVersion}. Sentry KMP uses private Cocoa APIs; " +
                "overriding the version may cause linking or runtime failures. Continuing with $cocoaVersion.",
        )
    }
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
