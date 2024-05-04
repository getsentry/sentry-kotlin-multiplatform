package io.sentry.kotlin.multiplatform.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class SentryExtension
@Inject
constructor(project: Project) {
    private val objects = project.objects

    val sentryCocoaVersion: Property<String> = objects.property(String::class.java)

    /**
     * When enabled the plugin will download the Sentry.xcframework and link the framework with linker-opts.
     * This fixes the issue with the Sentry framework not being found when running tests on Apple targets.
     * For example when trying to run tests using SPM.
     *
     * Defaults to `true` but is automatically **disabled** if the Cocoapods Gradle plugin is available.
     */
    val enableSentryTestLinking: Property<Boolean> =
        objects.property(Boolean::class.java).convention(true)
}
