package io.sentry.kotlin.multiplatform.gradle

import io.sentry.BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

/** Controls Apple dependency installation independently of commonMain installation. */
@Suppress("UnnecessaryAbstractClass")
abstract class AppleAutoInstallExtension
    @Inject
    constructor(
        project: Project,
    ) {
        private val objects = project.objects

        /**
         * Selects the integration that installs Sentry Cocoa. Defaults to [AppleDependencyProvider.AUTO].
         *
         * AUTO follows the recommended integration for the project and may change in a major release:
         * spm4Kmp when that plugin is applied; nothing when the Kotlin CocoaPods plugin is applied;
         * otherwise official SwiftPM on Kotlin 2.4 or newer.
         *
         * An explicitly selected provider that is unavailable fails the build. NONE installs nothing but
         * keeps commonMain installation and manual framework linking.
         */
        val provider: Property<AppleDependencyProvider> =
            objects
                .property(AppleDependencyProvider::class.java)
                .convention(AppleDependencyProvider.AUTO)

        /**
         * Overrides the Sentry Cocoa version installed by spm4Kmp or official SwiftPM.
         *
         * Requires an exact version. Defaults to the Sentry Cocoa version this plugin was built against.
         */
        val sentryCocoaVersion: Property<String> =
            objects.property(String::class.java).convention(BuildConfig.SentryCocoaVersion)
    }
