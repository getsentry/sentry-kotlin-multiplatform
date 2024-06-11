package io.sentry.kotlin.multiplatform.gradle

import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.provider.Property

@Suppress("UnnecessaryAbstractClass")
abstract class AutoInstallExtension @Inject constructor(project: Project) {
  private val objects = project.objects

  /**
   * Enable auto-installation of the Sentry dependencies through [CocoapodsAutoInstallExtension]
   *
   * Disabling this will prevent the plugin from auto installing anything.
   *
   * Defaults to true.
   */
  val enabled: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

  val cocoapods: CocoapodsAutoInstallExtension =
      objects.newInstance(CocoapodsAutoInstallExtension::class.java, project)

  val commonMain: SourceSetAutoInstallExtension =
      objects.newInstance(SourceSetAutoInstallExtension::class.java, project)
}
