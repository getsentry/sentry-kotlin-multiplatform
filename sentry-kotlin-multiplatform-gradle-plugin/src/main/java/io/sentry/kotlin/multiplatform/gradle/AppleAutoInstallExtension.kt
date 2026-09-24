package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

/** Controls Apple dependency installation independently of commonMain installation. */
@Suppress("UnnecessaryAbstractClass")
abstract class AppleAutoInstallExtension
    @Inject
    constructor(
        project: Project,
    ) {
        /**
         * Selects the Apple dependency integration. Defaults to [AppleDependencyProvider.AUTO].
         * AUTO considers official SwiftPM in use only when the application declares dependencies.
         * NONE disables Apple registration without disabling commonMain installation or linking.
         * When spm4Kmp is applied before Sentry, configure this before creating Kotlin targets.
         */
        val provider: Property<AppleDependencyProvider> =
            project.objects
                .property(AppleDependencyProvider::class.java)
                .convention(AppleDependencyProvider.AUTO)
    }
