package io.sentry.kotlin.multiplatform.gradle

import io.sentry.BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class Spm4KmpAutoInstallExtension
    @Inject
    constructor(
        project: Project,
    ) {
        private val objects = project.objects

        /**
         * Enable auto-installation of the Sentry Cocoa SDK Swift package via spm4Kmp.
         *
         * Adds Sentry Cocoa to every Apple target when spm4Kmp is applied and no user-defined Sentry
         * package config exists.
         *
         * Takes precedence over CocoaPods auto-install. Remove any manually declared Sentry pod
         * when adopting SPM, or disable this option to keep using CocoaPods.
         *
         * Requires the Sentry plugin to be applied before spm4Kmp unless disabled.
         * Can be set anywhere in the build script.
         *
         * Auto-installed packages require iOS/tvOS 15, macOS 12, and watchOS 9 or later.
         * Existing user-owned Sentry packages are left unchanged; configure these minimums there
         * when using Cocoa 9.28.0. watchosArm32 uses a no-op SDK and needs no Cocoa dependency.
         *
         * Defaults to true.
         */
        val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

        /**
         * Overrides default Sentry Cocoa version.
         *
         * Requires an exact version. Defaults to this plugin's Sentry Cocoa version.
         */
        val sentryCocoaVersion: Property<String> =
            objects.property(String::class.java).convention(BuildConfig.SentryCocoaVersion)
    }
