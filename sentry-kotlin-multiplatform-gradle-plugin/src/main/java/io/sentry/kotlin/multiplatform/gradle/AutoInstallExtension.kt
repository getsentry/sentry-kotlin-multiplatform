package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class AutoInstallExtension
    @Inject
    constructor(
        project: Project,
    ) {
        private val objects = project.objects

        /** Apple dependency provider selection. */
        val apple: AppleAutoInstallExtension = objects.newInstance(AppleAutoInstallExtension::class.java, project)

        /**
         * Enable auto-installation of Sentry dependencies through [AppleAutoInstallExtension]
         * and [SourceSetAutoInstallExtension].
         *
         * Disabling this also removes the plugin ordering requirement for spm4Kmp auto-install.
         *
         * Defaults to true.
         */
        val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

        /** Migration entry point for the removed CocoaPods integration. */
        @Deprecated("CocoaPods is unsupported with Sentry Cocoa 9. Use autoInstall.apple.provider instead.")
        @Suppress("DEPRECATION")
        val cocoapods: CocoapodsAutoInstallExtension
            get() = throw GradleException(
                "Sentry Cocoa 9 does not support CocoaPods. Remove sentryKmp.autoInstall.cocoapods " +
                    "configuration and any Sentry pod declaration, and use autoInstall.apple.provider instead.",
            )

        val commonMain: SourceSetAutoInstallExtension =
            objects.newInstance(SourceSetAutoInstallExtension::class.java, project)
    }
