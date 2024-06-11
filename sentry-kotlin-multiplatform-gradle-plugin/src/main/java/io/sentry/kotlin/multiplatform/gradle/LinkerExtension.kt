package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class LinkerExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    val xcodeprojPath: Property<String> = objects.property(String::class.java)
}
