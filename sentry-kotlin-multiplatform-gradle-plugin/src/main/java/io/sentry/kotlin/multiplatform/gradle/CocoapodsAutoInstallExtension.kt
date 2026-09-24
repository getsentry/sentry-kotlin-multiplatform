package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

/** Compatibility type for the removed CocoaPods configuration. */
@Deprecated("CocoaPods is unsupported with Sentry Cocoa 9. Use Spm4KmpAutoInstallExtension instead.")
@Suppress("UnnecessaryAbstractClass")
abstract class CocoapodsAutoInstallExtension
    @Inject
    constructor(
        project: Project,
    ) {
        private val objects = project.objects

        /** Retained only so obsolete configuration can report a migration error. */
        val enabled: Property<Boolean> = objects.property(Boolean::class.java)

        /** Retained only so obsolete configuration can report a migration error. */
        val sentryCocoaVersion: Property<String> = objects.property(String::class.java)
    }
