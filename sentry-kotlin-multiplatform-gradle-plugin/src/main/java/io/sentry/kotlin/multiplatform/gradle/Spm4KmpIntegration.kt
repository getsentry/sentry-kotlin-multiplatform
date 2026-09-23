package io.sentry.kotlin.multiplatform.gradle

import io.github.frankois944.spmForKmp.swiftPackageConfig
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager
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
    hostIsMac: Boolean = HostManager.hostIsMac
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
    kmpExtension.appleTargets().forEach { target ->
        if (userDefinedConfigNames.isNotEmpty()) {
            if (!isSentryConfiguredViaSpm4Kmp(target.name)) {
                logger.warn(
                    "Sentry Cocoa is configured with spm4Kmp in $userDefinedConfigNames, so the " +
                        "auto-install left ${target.name} alone rather than add a second " +
                        "sentry-cocoa package that would override that configuration. Add " +
                        "${target.name} to your own swiftPackageConfig if it needs Sentry Cocoa."
                )
            } else {
                logger.info(
                    "Sentry Cocoa Swift package already configured for ${target.name}. " +
                        "Skipping spm4Kmp auto installation."
                )
            }
            return@forEach
        }

        val cocoaVersion = autoInstall.spm.sentryCocoaVersion.get()
        target.swiftPackageConfig(cinteropName = SENTRY_COCOA_CINTEROP_NAME) {
            dependency {
                remotePackageVersion(
                    url = URI(SENTRY_COCOA_GIT_URL),
                    version = cocoaVersion,
                    products = {
                        // Link only (exportToKotlin defaults to false): the published klib already
                        // carries the Sentry cinterop bindings.
                        add("Sentry")
                    }
                )
            }
        }
        logger.lifecycle(
            "Registered the Sentry Cocoa $cocoaVersion Swift package with spm4Kmp for ${target.name}."
        )
    }
}
