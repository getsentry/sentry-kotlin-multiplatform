package io.sentry.kotlin.multiplatform.gradle

import javax.inject.Inject
import org.gradle.api.Project

@Suppress("UnnecessaryAbstractClass")
abstract class SentryExtension @Inject constructor(project: Project) {
  private val objects = project.objects

  /**
   * Linker configuration.
   *
   * If you use SPM this configuration is necessary for setting up linking the framework and test
   * executable.
   */
  val linker: LinkerExtension = objects.newInstance(LinkerExtension::class.java, project)

  val autoInstall: AutoInstallExtension =
      objects.newInstance(AutoInstallExtension::class.java, project)
}
