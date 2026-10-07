package io.sentry.kotlin.multiplatform.gradle

import io.sentry.BuildConfig
import io.sentry.kotlin.multiplatform.gradle.AppleDependencyProvider.AUTO
import io.sentry.kotlin.multiplatform.gradle.AppleDependencyProvider.NONE
import io.sentry.kotlin.multiplatform.gradle.AppleDependencyProvider.SPM4KMP
import io.sentry.kotlin.multiplatform.gradle.AppleDependencyProvider.SWIFT_PM
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.KotlinCocoapodsPlugin
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

internal const val SWIFTPM_IMPORT_DOCS = "https://kotlinlang.org/docs/multiplatform/multiplatform-spm-import.html"

// Keep new Kotlin types out of the entrypoint: this also runs with Kotlin 2.2.
internal fun Project.officialSwiftPmExtension(): Any? {
    if (!plugins.hasPlugin(KOTLIN_MULTIPLATFORM_PLUGIN_ID)) return null
    val version = GradleVersion.version(getKotlinPluginVersion().substringBefore('-'))
    if (version < GradleVersion.version("2.4.0")) return null
    return (extensions.findByName(KOTLIN_EXTENSION_NAME) as? ExtensionAware)
        ?.extensions
        ?.findByName("swiftPMDependencies")
}

/**
 * AUTO prefers spm4Kmp, installs nothing next to the CocoaPods plugin (Kotlin rejects SwiftPM
 * dependencies there), and otherwise uses official SwiftPM on Kotlin 2.4+. An explicitly selected
 * provider that is unavailable fails the build instead of falling back.
 */
internal fun Project.resolveAppleDependencyProvider(autoInstall: AutoInstallExtension): AppleDependencyProvider {
    if (!autoInstall.enabled.get()) return NONE
    return when (val requested = autoInstall.apple.provider.get()) {
        AUTO ->
            when {
                hasSpm4Kmp() -> SPM4KMP
                hasCocoapods() -> NONE
                officialSwiftPmExtension() != null -> SWIFT_PM
                else -> NONE
            }
        SWIFT_PM -> {
            if (hasCocoapods()) {
                throw GradleException(
                    "Sentry Apple provider SWIFT_PM cannot be used with the Kotlin CocoaPods plugin: Kotlin " +
                        "rejects SwiftPM dependencies in CocoaPods projects. Select NONE and configure " +
                        "sentryKmp.linker, or migrate to SwiftPM (https://kotl.in/cocoapods-to-swiftpm-migration).",
                )
            }
            requested.requireAvailable(
                officialSwiftPmExtension() != null,
                "it requires Kotlin 2.4+ with SwiftPM import (found Kotlin ${kotlinPluginVersion()}). Upgrade Kotlin",
            )
        }
        SPM4KMP -> requested.requireAvailable(hasSpm4Kmp(), "apply the spm4Kmp plugin ($SPM4KMP_PLUGIN_ID)")
        NONE -> NONE
    }
}

/** Explains why no Sentry Cocoa dependency was registered, for a resolved provider of NONE. */
internal fun Project.noAppleDependencyReason(requested: AppleDependencyProvider): String =
    when {
        requested == NONE -> "sentryKmp.autoInstall.apple.provider is NONE."
        hasCocoapods() -> "the Kotlin CocoaPods plugin is applied, and Kotlin rejects SwiftPM dependencies there."
        else ->
            "Kotlin ${kotlinPluginVersion()} has no official SwiftPM import and spm4Kmp is not applied " +
                "(upgrade to Kotlin 2.4+ or apply spm4Kmp for automatic installation)."
    }

/**
 * Lists recovery steps for when this plugin cannot call Kotlin's experimental SwiftPM import API,
 * e.g. because a newer Kotlin changed it or the build loads several Kotlin Gradle plugin versions.
 */
internal fun Project.swiftPmIncompatibilityMessage(cocoaVersion: String): String {
    val kotlinVersion = kotlinPluginVersion()
    return """
        |Sentry KMP Gradle plugin ${BuildConfig.SentryKmpVersion} could not install Sentry Cocoa with Kotlin $kotlinVersion.
        |
        |Try these steps in order:
        |
        |  1. Use the same Kotlin Gradle plugin version in all modules, including buildSrc and included builds.
        |
        |  2. Update the Sentry KMP Gradle plugin (io.sentry.kotlin.multiplatform.gradle) to the latest version.
        |     If you already use it, please report this at https://github.com/getsentry/sentry-kotlin-multiplatform/issues.
        |
        |  3. If you recently upgraded Kotlin, use Kotlin ${BuildConfig.KotlinGradlePluginVersion} until a newer Sentry KMP Gradle plugin
        |     supports your Kotlin version.
        |
        |  4. Install Sentry Cocoa yourself. Add this to the module's build.gradle.kts:
        |
        |        import io.sentry.kotlin.multiplatform.gradle.AppleDependencyProvider
        |
        |        sentryKmp {
        |            autoInstall.apple.provider.set(AppleDependencyProvider.NONE)
        |            // Kotlin links packages from swiftPMDependencies itself.
        |            linker.enabled.set(false)
        |        }
        |
        |        kotlin {
        |            swiftPMDependencies {
        |                swiftPackage(
        |                    url = url("$SENTRY_COCOA_GIT_URL"),
        |                    version = exact("$cocoaVersion"),
        |                    products = listOf(product("Sentry")),
        |                )
        |                // Only if you target macOS or tvOS, and use a higher version if your app needs it.
        |                macosMinimumDeploymentTarget.set("$SENTRY_COCOA_MIN_MACOS")
        |                tvosMinimumDeploymentTarget.set("$SENTRY_COCOA_MIN_TVOS")
        |            }
        |        }
        |
        |     Keep Sentry Cocoa at exactly $cocoaVersion. If the snippet does not compile, see
        |     $SWIFTPM_IMPORT_DOCS for the current syntax.
        |
        |     Or add sentry-cocoa $cocoaVersion to your Xcode project with Swift Package Manager, and set only
        |     sentryKmp.autoInstall.apple.provider to NONE.
        """.trimMargin()
}

private fun Project.hasSpm4Kmp(): Boolean = plugins.hasPlugin(SPM4KMP_PLUGIN_ID)

private fun Project.hasCocoapods(): Boolean = plugins.hasPlugin(KotlinCocoapodsPlugin::class.java)

private fun Project.kotlinPluginVersion(): String = runCatching { getKotlinPluginVersion() }.getOrDefault("unknown")

private fun AppleDependencyProvider.requireAvailable(
    available: Boolean,
    fix: String,
): AppleDependencyProvider {
    if (!available) {
        throw GradleException(
            "Sentry Apple provider $this is unavailable: $fix, " +
                "or select AUTO or NONE in sentryKmp.autoInstall.apple.provider.",
        )
    }
    return this
}

/** Only checks declarations visible through public APIs: spm4Kmp configs and CocoaPods pods. */
internal fun Project.warnOnConflictingAppleDependencies(resolved: AppleDependencyProvider) {
    val kotlin = extensions.findByName(KOTLIN_EXTENSION_NAME) as? KotlinMultiplatformExtension ?: return
    if (resolved == SWIFT_PM && kotlin.appleTargets().any { isSentryConfiguredViaSpm4Kmp(it.name) }) {
        logger.warn(
            "Sentry Cocoa is configured through spm4Kmp and also installed through official SwiftPM. " +
                "Remove the spm4Kmp Sentry configuration or select a different sentryKmp.autoInstall.apple.provider.",
        )
    }
    val pods = (kotlin as ExtensionAware).extensions.findByType(CocoapodsExtension::class.java)
    if (pods?.pods?.findByName("Sentry") != null) {
        logger.warn(
            "A Sentry pod is declared, but Sentry Cocoa 9 does not support CocoaPods. " +
                "Remove the Sentry pod declaration.",
        )
    }
}
