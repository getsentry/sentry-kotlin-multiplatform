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
     * If the spm4Kmp plugin (io.github.frankois944.spmForKmp) is applied and no existing Sentry
     * Swift package configuration exists, the Sentry-Cocoa SDK will be added to every Apple target.
     *
     * Can be set anywhere in the build script, as long as this plugin is applied before the spm4Kmp
     * plugin. If spm4Kmp is applied first, the Swift package has to be registered as each Apple
     * target is created, so this must then be set before the `kotlin { }` block declares them.
     *
     * Defaults to true.
     */
    val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    /**
     * Overrides default Sentry Cocoa version.
     *
     * Can be set anywhere in the build script, as long as this plugin is applied before the spm4Kmp
     * plugin. If spm4Kmp is applied first, the Swift package has to be registered as each Apple
     * target is created, so this must then be set before the `kotlin { }` block declares them.
     *
     * Defaults to the version used in the latest KMP SDK. Must be an exact version since the Swift
     * Package Manager resolves remote packages by exact version.
     */
    val sentryCocoaVersion: Property<String> =
        objects.property(String::class.java).convention(BuildConfig.SentryCocoaVersion)
}
