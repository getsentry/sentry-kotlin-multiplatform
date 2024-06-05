package io.sentry.kotlin.multiplatform.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class SentryExtension
@Inject
constructor(project: Project) {
    private val objects = project.objects

    /**
     * Auto-installs the Sentry-Cocoa SDK pod if the cocoapods plugin is enabled.
     */
    val autoInstallWithCocoapods: Property<Boolean> =
        objects.property(Boolean::class.java).convention(true)

    /**
     * Linker configuration.
     *
     * If you use SPM this configuration is necessary for setting up linking the framework and test executable.
     */
    val linker: LinkerExtension = objects.newInstance(LinkerExtension::class.java, project)
}
