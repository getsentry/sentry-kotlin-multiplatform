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
     * Disabling this will prevent the plugin from auto installing any dependency.
     *
     * The spm4Kmp auto-install registers the Sentry Swift package as soon as each Apple target is
     * created, so to disable it this flag must be set before the `kotlin { }` block declares the
     * Apple targets — setting it afterwards only disables the CocoaPods and commonMain installs.
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
