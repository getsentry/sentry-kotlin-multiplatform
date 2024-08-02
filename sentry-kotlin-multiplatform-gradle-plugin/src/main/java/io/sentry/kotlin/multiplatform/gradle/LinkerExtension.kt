package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class LinkerExtension @Inject constructor(project: Project) {
    private val objects = project.objects

    /**
     * Path to the Xcode project that will be used to link the framework.
     * This is used to find the derived data path in which the framework is stored for SPM.
     */
    val xcodeprojPath: Property<String> = objects.property(String::class.java)

    /**
     * Path to the framework that will be linked.
     * Takes precedence over [xcodeprojPath] if both are set.
     */
    val frameworkPath: Property<String> = objects.property(String::class.java)
}
