package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.Input
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class DerivedDataPathValueSource :
    ValueSource<String, DerivedDataPathValueSource.Parameters> {
    interface Parameters : ValueSourceParameters {
        @get:Input
        val xcodeprojPath: Property<String>
    }

    @get:Inject
    abstract val execOperations: ExecOperations

    companion object {
        private val buildDirRegex = Regex("BUILD_DIR = (.+)")
    }

    override fun obtain(): String {
        val buildDirOutput = ByteArrayOutputStream()
        execOperations.exec {
            it.commandLine = listOf(
                "xcodebuild",
                "-project",
                parameters.xcodeprojPath.get(),
                "-showBuildSettings"
            )
            it.standardOutput = buildDirOutput
        }
        val buildSettings = buildDirOutput.toString("UTF-8")
        val buildDirMatch = buildDirRegex.find(buildSettings)
        val buildDir = buildDirMatch?.groupValues?.get(1)
            ?: throw GradleException("BUILD_DIR not found in xcodebuild output")
        return buildDir.replace("/Build/Products", "")
    }
}
