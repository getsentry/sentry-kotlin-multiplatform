package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class SourceSetAutoInstallExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    /**
     * Enable auto-installation of the Sentry Kotlin Multiplatform SDK dependency in the commonMain
     * source set.
     *
     * Defaults to true.
     */
    val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    /**
     * Overrides the default Sentry Kotlin Multiplatform SDK dependency version.
     *
     * Defaults to the latest version of the Sentry Kotlin Multiplatform SDK which is
     * the same as the version of the Sentry Gradle Plugin.
     */
    val sentryKmpVersion: Property<String> =
        objects
            .property(String::class.java)
            .convention(project.version.toString())
}
