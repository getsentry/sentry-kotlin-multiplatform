@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package io.sentry.kotlin.multiplatform.gradle

import io.sentry.BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.swiftimport.SwiftPMDependency
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.swiftimport.SwiftPMImportExtension
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget

/**
 * Kotlin 2.4 exposes package registration publicly, but declaration inspection is internal.
 * Confine that dependency here and test this adapter against each supported Kotlin version.
 * Never load this object unless the official SwiftPM extension is available.
 */
internal object OfficialSwiftPmIntegration {
    // These Cocoa products supply the Sentry module; ObjC-only products do not.
    private val sentryProducts =
        setOf(
            "Sentry",
            "Sentry-Dynamic",
            "Sentry-Dynamic-WithARM64e",
            "SentrySwiftUI",
            "Sentry-WithoutUIKitOrAppKit",
            "Sentry-WithoutUIKitOrAppKit-WithARM64e",
        )

    fun isInUse(extension: Any): Boolean = (extension as SwiftPMImportExtension).swiftPMDependencies.isNotEmpty()

    fun hasSentry(extension: Any): Boolean = sentryDependencies(extension).isNotEmpty()

    fun covers(
        extension: Any,
        target: KonanTarget,
    ): Boolean {
        val platform = platform(target) ?: return false
        return sentryDependencies(extension).any { dependency ->
            dependency.products.any { product ->
                val constraints = product.platformConstraints
                product.name in sentryProducts && (constraints == null || platform in constraints)
            }
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    fun install(
        project: Project,
        extension: Any,
    ) {
        val kotlin = project.extensions.getByName(KOTLIN_EXTENSION_NAME) as KotlinMultiplatformExtension
        val platforms = kotlin.appleTargets().mapNotNull { platform(it.konanTarget) }.toSet()
        if (platforms.isEmpty()) return
        val existing = sentryDependencies(extension)
        if (existing.isNotEmpty()) {
            existing.filterIsInstance<SwiftPMDependency.Remote>().forEach { dependency ->
                val version = dependency.version as? SwiftPMDependency.Remote.Version.Exact
                if (version != null && version.value != BuildConfig.SentryCocoaVersion) {
                    project.logger.warn(
                        "Official SwiftPM declares Sentry Cocoa ${version.value}; this Sentry plugin expects " +
                            "${BuildConfig.SentryCocoaVersion}. Keeping the application's declaration.",
                    )
                }
            }
            project.logger.info(
                "Sentry Cocoa is already declared through official SwiftPM; keeping its products and platforms.",
            )
            return
        }
        val swift = extension as SwiftPMImportExtension
        swift.swiftPackage(
            url = swift.url("https://github.com/getsentry/sentry-cocoa.git"),
            version = swift.exact(BuildConfig.SentryCocoaVersion),
            products = listOf(swift.product("Sentry", platforms = platforms, importedClangModules = emptySet())),
            importedClangModules = emptyList(),
        )
        if (SwiftPMDependency.Platform.iOS in platforms) swift.iosMinimumDeploymentTarget.raiseMinimum("15.0")
        if (SwiftPMDependency.Platform.macOS in platforms) swift.macosMinimumDeploymentTarget.raiseMinimum("12.0")
        if (SwiftPMDependency.Platform.tvOS in platforms) swift.tvosMinimumDeploymentTarget.raiseMinimum("15.0")
        if (SwiftPMDependency.Platform.watchOS in platforms) swift.watchosMinimumDeploymentTarget.raiseMinimum("9.0")
        project.logger.lifecycle("Registered Sentry Cocoa ${BuildConfig.SentryCocoaVersion} through official SwiftPM.")
    }

    private fun sentryDependencies(extension: Any): List<SwiftPMDependency> =
        (extension as SwiftPMImportExtension).swiftPMDependencies.filter { dependency ->
            val repository =
                (dependency as? SwiftPMDependency.Remote)?.repository as? SwiftPMDependency.Remote.Repository.Url
            val sentryUrl =
                repository?.value?.trimEnd('/')?.removeSuffix(".git") == "https://github.com/getsentry/sentry-cocoa"
            sentryUrl || dependency.products.any { it.name in sentryProducts }
        }

    private fun platform(target: KonanTarget): SwiftPMDependency.Platform? =
        if (target == KonanTarget.WATCHOS_ARM32) {
            null
        } else {
            when (target.family) {
                Family.IOS -> SwiftPMDependency.Platform.iOS
                Family.OSX -> SwiftPMDependency.Platform.macOS
                Family.TVOS -> SwiftPMDependency.Platform.tvOS
                Family.WATCHOS -> SwiftPMDependency.Platform.watchOS
                else -> null
            }
        }

    private fun Property<String>.raiseMinimum(minimum: String) {
        set(minimumDeploymentVersion(orNull, minimum))
    }
}
