package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.KotlinCocoapodsPlugin
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

// Keep new Kotlin types out of the entrypoint: this also runs with Kotlin 2.2.
internal fun Project.officialSwiftPmExtension(): Any? {
    if (!plugins.hasPlugin(KOTLIN_MULTIPLATFORM_PLUGIN_ID)) return null
    val version = GradleVersion.version(getKotlinPluginVersion().substringBefore('-'))
    if (version < GradleVersion.version("2.4.0")) return null
    return (extensions.findByName(KOTLIN_EXTENSION_NAME) as? ExtensionAware)
        ?.extensions
        ?.findByName("swiftPMDependencies")
}

internal fun Project.resolveAppleDependencyProvider(
    autoInstall: AutoInstallExtension,
    validate: Boolean = true,
): AppleDependencyProvider {
    if (!autoInstall.enabled.get()) return AppleDependencyProvider.NONE
    val requested = autoInstall.apple.provider.get()
    if (requested == AppleDependencyProvider.AUTO) {
        return detectAppleDependencyProvider(autoInstall)
    }
    if (!autoInstall.isProviderEnabled(requested)) return AppleDependencyProvider.NONE
    val available = isAppleProviderAvailable(requested)
    if (validate && !available) {
        throw GradleException(
            "Sentry Apple provider $requested is unavailable. Apply its integration " +
                "(official SwiftPM requires Kotlin 2.4+ with SwiftPM import enabled), " +
                "or select AUTO/NONE in sentryKmp.autoInstall.apple.provider.",
        )
    }
    return if (available) requested else AppleDependencyProvider.NONE
}

private fun Project.detectAppleDependencyProvider(autoInstall: AutoInstallExtension): AppleDependencyProvider {
    val swift = officialSwiftPmExtension()
    return when {
        swift != null && OfficialSwiftPmIntegration.isInUse(swift) -> AppleDependencyProvider.SWIFT_PM
        isAppleProviderAvailable(AppleDependencyProvider.SPM4KMP) && autoInstall.spm.enabled.get() ->
            AppleDependencyProvider.SPM4KMP
        isAppleProviderAvailable(AppleDependencyProvider.COCOAPODS) && autoInstall.cocoapods.enabled.get() ->
            AppleDependencyProvider.COCOAPODS
        else -> AppleDependencyProvider.NONE
    }
}

private fun AutoInstallExtension.isProviderEnabled(provider: AppleDependencyProvider): Boolean =
    when (provider) {
        AppleDependencyProvider.SPM4KMP -> spm.enabled.get()
        AppleDependencyProvider.COCOAPODS -> cocoapods.enabled.get()
        else -> true
    }

private fun Project.isAppleProviderAvailable(provider: AppleDependencyProvider): Boolean =
    when (provider) {
        AppleDependencyProvider.SWIFT_PM -> officialSwiftPmExtension() != null
        AppleDependencyProvider.SPM4KMP -> plugins.hasPlugin(SPM4KMP_PLUGIN_ID)
        AppleDependencyProvider.COCOAPODS -> plugins.hasPlugin(KotlinCocoapodsPlugin::class.java)
        else -> true
    }

internal fun Project.isSentryConfiguredViaOfficialSwiftPm(target: KotlinNativeTarget): Boolean =
    officialSwiftPmExtension()?.let { OfficialSwiftPmIntegration.covers(it, target.konanTarget) } ?: false

internal fun Project.warnOnConflictingAppleDependencies() {
    val kotlin = extensions.findByName(KOTLIN_EXTENSION_NAME) as? KotlinMultiplatformExtension ?: return
    val providers = mutableListOf<String>()
    if (officialSwiftPmExtension()?.let { OfficialSwiftPmIntegration.hasSentry(it) } == true) {
        providers += "official SwiftPM"
    }
    if (kotlin.appleTargets().any { isSentryConfiguredViaSpm4Kmp(it.name) }) providers += "spm4Kmp"
    val pods = (kotlin as ExtensionAware).extensions.findByType(CocoapodsExtension::class.java)
    if (pods?.pods?.findByName("Sentry") != null) providers += "CocoaPods"
    if (providers.size > 1) {
        logger.warn(
            "Sentry Cocoa is declared through multiple integrations: $providers. " +
                "Remove duplicate manual Sentry declarations and select the intended " +
                "sentryKmp.autoInstall.apple.provider. Provider priority does not remove user declarations " +
                "or make CocoaPods and official SwiftPM compatible.",
        )
    }
}
