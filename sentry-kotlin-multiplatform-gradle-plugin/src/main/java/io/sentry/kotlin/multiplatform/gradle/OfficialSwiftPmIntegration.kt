package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.swiftimport.SwiftPMDependency.Platform
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.swiftimport.SwiftPMImportExtension
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget

// Kotlin 2.4's minimums for unset deployment targets (GenerateSyntheticLinkageImportProject).
private const val KOTLIN_DEFAULT_MIN_IOS = "15.0"
private const val KOTLIN_DEFAULT_MIN_MACOS = "10.15"
private const val KOTLIN_DEFAULT_MIN_TVOS = "9.0"
private const val KOTLIN_DEFAULT_MIN_WATCHOS = "15.0"

/**
 * Registers Sentry Cocoa through Kotlin 2.4's official SwiftPM import using only its public API.
 * Declarations are never read back: their accessors are internal to the Kotlin Gradle plugin.
 *
 * Never load this object unless [officialSwiftPmExtension] returned an extension; it is passed
 * as [Any] so callers do not reference Kotlin 2.4 types.
 */
internal object OfficialSwiftPmIntegration {
    /** Returns the names of the targets that the registered package supplies. */
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    fun install(
        project: Project,
        extension: Any,
        cocoaVersion: String,
    ): Set<String> {
        val kotlin = project.extensions.getByName(KOTLIN_EXTENSION_NAME) as KotlinMultiplatformExtension
        val targetPlatforms =
            kotlin
                .appleTargets()
                .mapNotNull { target -> target.konanTarget.swiftPmPlatform()?.let { target.name to it } }
                .toMap()
        if (targetPlatforms.isEmpty()) return emptySet()

        project.warnIfCocoaVersionMismatch(cocoaVersion)
        val platforms = targetPlatforms.values.toSet()
        val swift = extension as SwiftPMImportExtension
        swift.swiftPackage(
            url = swift.url(SENTRY_COCOA_GIT_URL),
            version = swift.exact(cocoaVersion),
            products = listOf(swift.product("Sentry", platforms = platforms, importedClangModules = emptySet())),
            importedClangModules = emptyList(),
        )
        if (Platform.iOS in platforms) {
            swift.iosMinimumDeploymentTarget.raiseMinimum(SENTRY_COCOA_MIN_IOS, KOTLIN_DEFAULT_MIN_IOS)
        }
        if (Platform.macOS in platforms) {
            swift.macosMinimumDeploymentTarget.raiseMinimum(SENTRY_COCOA_MIN_MACOS, KOTLIN_DEFAULT_MIN_MACOS)
        }
        if (Platform.tvOS in platforms) {
            swift.tvosMinimumDeploymentTarget.raiseMinimum(SENTRY_COCOA_MIN_TVOS, KOTLIN_DEFAULT_MIN_TVOS)
        }
        if (Platform.watchOS in platforms) {
            swift.watchosMinimumDeploymentTarget.raiseMinimum(SENTRY_COCOA_MIN_WATCHOS, KOTLIN_DEFAULT_MIN_WATCHOS)
        }

        project.logger.lifecycle(
            "Registered Sentry Cocoa $cocoaVersion via official SwiftPM. If you also added sentry-cocoa in Xcode, " +
                "remove it or set sentryKmp.autoInstall.apple.provider = NONE.",
        )
        project.logger.info(
            "Official SwiftPM requires Kotlin's one-time Xcode linkage-package integration: $SWIFTPM_IMPORT_DOCS",
        )
        return targetPlatforms.keys
    }

    private fun KonanTarget.swiftPmPlatform(): Platform? =
        when {
            this == KonanTarget.WATCHOS_ARM32 -> null
            family == Family.IOS -> Platform.iOS
            family == Family.OSX -> Platform.macOS
            family == Family.TVOS -> Platform.tvOS
            family == Family.WATCHOS -> Platform.watchOS
            else -> null
        }

    /**
     * An unset minimum lets Kotlin use the highest of [kotlinDefault] and the minimums of transitive
     * SwiftPM dependencies; setting it replaces that maximum. Only set it when Kotlin's default is too low.
     */
    private fun Property<String>.raiseMinimum(
        minimum: String,
        kotlinDefault: String,
    ) {
        val configured = orNull
        if (configured == null && minimumDeploymentVersion(kotlinDefault, minimum) == kotlinDefault) return
        set(minimumDeploymentVersion(configured, minimum))
    }
}
