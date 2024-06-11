package io.sentry.kotlin.multiplatform.gradle

import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.provider.Property

@Suppress("UnnecessaryAbstractClass")
abstract class LinkerExtension @Inject constructor(project: Project) {
  private val objects = project.objects

  val xcodeprojPath: Property<String> = objects.property(String::class.java)
}
