package io.sentry.kotlin.multiplatform.gradle

import io.sentry.BuildConfig
import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class Spm4KmpAutoInstallExtension
@Inject
constructor(
    project: Project
) {
    private val objects = project.objects

    /**
     * Enable auto-installation of the Sentry Cocoa SDK Swift package via spm4Kmp.
     *
     * Adds Sentry Cocoa to every Apple target when spm4Kmp is applied and no user-defined Sentry
     * package config exists.
     *
     * If spm4Kmp is applied before Sentry, set this before `kotlin { }`.
     * Otherwise, it can be set anywhere in the build script.
     *
     * Defaults to true.
     */
    val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    /**
     * Overrides default Sentry Cocoa version.
     *
     * If spm4Kmp is applied before Sentry, set this before `kotlin { }`.
     * Otherwise, it can be set anywhere in the build script.
     *
     * Requires an exact version. Defaults to this plugin's Sentry Cocoa version.
     */
    val sentryCocoaVersion: Property<String> =
        objects.property(String::class.java).convention(BuildConfig.SentryCocoaVersion)
}
