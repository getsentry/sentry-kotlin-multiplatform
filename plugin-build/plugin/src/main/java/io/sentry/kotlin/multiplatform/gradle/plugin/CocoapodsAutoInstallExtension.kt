package io.sentry.kotlin.multiplatform.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class CocoapodsAutoInstallExtension
@Inject
constructor(project: Project) {
    private val objects = project.objects

    /**
     * Enable auto-installation of the Sentry Cocoa SDK pod.
     *
     * If the cocoapods plugin is applied and no existing Sentry pod configuration exists, the Sentry-Cocoa SDK pod will be installed
     *
     * Defaults to true.
     */
    val enabled: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(true)

    /**
     * Overrides default Sentry Cocoa version.
     *
     * Defaults to the `~> 8.25`.
     */
    val sentryCocoaVersion: Property<String> = objects.property(String::class.java)
        .convention("~> 8.25") // todo: grab the latest version properly
}
