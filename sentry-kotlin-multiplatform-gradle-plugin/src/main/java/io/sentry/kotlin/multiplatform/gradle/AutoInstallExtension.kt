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
     * If the spm4Kmp plugin is applied before this one, its Swift package has to be registered as
     * each Apple target is created, so disabling it then requires setting this flag before the
     * `kotlin { }` block declares the targets — setting it afterwards only disables the CocoaPods
     * and commonMain installs. Applying this plugin first lifts that restriction; see
     * [Spm4KmpAutoInstallExtension.enabled].
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
