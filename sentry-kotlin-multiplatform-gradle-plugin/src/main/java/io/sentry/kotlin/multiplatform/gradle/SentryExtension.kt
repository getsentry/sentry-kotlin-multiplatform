package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.Project
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class SentryExtension
    @Inject
    constructor(
        project: Project,
    ) {
        private val objects = project.objects

        /**
         * Linker configuration.
         *
         * Configures manual Cocoa framework discovery and linking. Targets for which official
         * SwiftPM or spm4Kmp supply Sentry Cocoa use that integration's linking instead.
         */
        val linker: LinkerExtension = objects.newInstance(LinkerExtension::class.java, project)

        val autoInstall: AutoInstallExtension =
            objects.newInstance(AutoInstallExtension::class.java, project)
    }
