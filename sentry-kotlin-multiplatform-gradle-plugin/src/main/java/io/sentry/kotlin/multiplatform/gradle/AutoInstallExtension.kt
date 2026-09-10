package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class AutoInstallExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    /**
     * Enable auto-installation of the Sentry dependencies through [CocoapodsAutoInstallExtension],
     * [Spm4KmpAutoInstallExtension] and [SourceSetAutoInstallExtension].
     *
     * If spm4Kmp is applied before Sentry, set this before `kotlin { }` to prevent Swift package
     * registration. See [Spm4KmpAutoInstallExtension.enabled].
     *
     * Defaults to true.
     */
    val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    val cocoapods: CocoapodsAutoInstallExtension =
        objects.newInstance(CocoapodsAutoInstallExtension::class.java, project)

    val spm: Spm4KmpAutoInstallExtension =
        objects.newInstance(Spm4KmpAutoInstallExtension::class.java, project)

    val commonMain: SourceSetAutoInstallExtension =
        objects.newInstance(SourceSetAutoInstallExtension::class.java, project)
}
