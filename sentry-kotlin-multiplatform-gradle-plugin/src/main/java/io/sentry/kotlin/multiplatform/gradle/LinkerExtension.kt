package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class LinkerExtension
    @Inject
    constructor(
        project: Project,
    ) {
        private val objects = project.objects

        /**
         * Configures linking of the Sentry Cocoa framework for Apple targets.
         *
         * Targets that already receive Sentry Cocoa through SwiftPM are skipped: official SwiftPM
         * dependencies registered by this plugin, and spm4Kmp cinterops named `sentryCocoa`, including
         * user-defined ones. For all other targets, the plugin resolves the framework location and fails
         * the build if the framework cannot be found.
         *
         * Keep this enabled when Sentry Cocoa is added through Xcode. Disable it when Sentry Cocoa is
         * declared manually in Kotlin's `swiftPMDependencies` or in an spm4Kmp cinterop with a different
         * name, since the plugin cannot detect those declarations.
         *
         * Defaults to `true`.
         */
        val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

        /**
         * Path to the Xcode project that will be used to link the framework.
         * This is used to find the derived data path in which the framework is stored for SPM.
         */
        val xcodeprojPath: Property<String> = objects.property(String::class.java)

        /**
         * Path to the framework that will be linked.
         * Takes precedence over [xcodeprojPath] if both are set.
         *
         * The path must:
         * 1. Point directly to the .xcframework folder
         * 2. The .xcframework folder needs to be either `Sentry.xcframework` or `Sentry-Dynamic.xcframework`
         *
         * ### Usage Example:
         * ```kotlin
         * sentryKmp {
         *     frameworkPath.set(
         *         "path/to/Sentry.xcframework" // Static framework
         *         // or
         *         "path/to/Sentry-Dynamic.xcframework" // Dynamic framework
         *     )
         * }
         * ```
         *
         * ### Typical Locations:
         *   `~/Library/Developer/Xcode/DerivedData/{PROJECT}/SourcePackages/artifacts/sentry-cocoa`
         */
        val frameworkPath: Property<String> = objects.property(String::class.java)
    }
