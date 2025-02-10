package io.sentry.kotlin.multiplatform.gradle

import io.sentry.kotlin.multiplatform.gradle.SentryPlugin.Companion.logger
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.Input
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * Provides the derived data path for an Xcode project using the xcodebuild command.
 *
 * e.g /Users/theusername/Library/Developer/Xcode/DerivedData/iosApp-ddefikekigqzzgcnpfkkdallksmlfpln/
 */
abstract class DerivedDataPathValueSource :
    ValueSource<String?, DerivedDataPathValueSource.Parameters> {
    interface Parameters : ValueSourceParameters {
        @get:Input
        val xcodeprojPath: Property<String>
    }

    @get:Inject
    abstract val execOperations: ExecOperations

    companion object {
        private val buildDirRegex = Regex("BUILD_DIR = (.+)")
    }

    override fun obtain(): String? {
        val buildDirOutput = ByteArrayOutputStream()
        val errOutput = ByteArrayOutputStream()

        val execOperations = execOperations.exec {
            it.commandLine = listOf(
                "xcodebuild",
                "-project",
                parameters.xcodeprojPath.get(),
                "-showBuildSettings"
            )
            it.standardOutput = buildDirOutput
            it.errorOutput = errOutput
        }

        if (execOperations.exitValue == 0) {
            val buildSettings = buildDirOutput.toString("UTF-8")
            val buildDirMatch = buildDirRegex.find(buildSettings)
            val buildDir = buildDirMatch?.groupValues?.get(1)
            if (buildDir == null || buildDir.contains("DerivedData").not()) {
                return null
            }
            return buildDir.replace("/Build/Products", "")
        } else {
            logger.warn(
                "Failed to retrieve derived data path. xcodebuild command failed. Error output: ${
                errOutput.toString(
                    Charsets.UTF_8
                )
                }"
            )
            return null
        }
    }
}
