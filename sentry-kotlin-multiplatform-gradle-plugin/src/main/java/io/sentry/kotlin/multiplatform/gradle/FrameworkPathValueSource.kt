package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File

abstract class FrameworkPathValueSource : ValueSource<String?, FrameworkPathValueSource.Parameters> {
    interface Parameters : ValueSourceParameters {
        val frameworkArchitectures: SetProperty<String>
        val frameworkType: Property<FrameworkType>
        val derivedDataPath: Property<String?>
    }

    @get:javax.inject.Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String? {
        val frameworkArchitectures = parameters.frameworkArchitectures.get()
        val frameworkType = parameters.frameworkType.get()
        val derivedDataPath = parameters.derivedDataPath.orNull

        frameworkArchitectures.forEach { architecture ->
            if (derivedDataPath != null) {
                val path = findFrameworkWithDerivedData(
                    frameworkType,
                    derivedDataPath = derivedDataPath,
                    frameworkArchitecture = architecture
                )
                if (path != null) {
                    return path
                }
            }

            // if we didn't find anything with derived data, let's fallback to using the find cmd
            val path = findFrameworkWithFindCommand(frameworkType, architecture)
            if (path != null) {
                return path
            }
        }

        return null
    }

    /**
     * Returns valid framework if exists with with the derived data path
     */
    private fun findFrameworkWithDerivedData(
        frameworkType: FrameworkType,
        derivedDataPath: String,
        frameworkArchitecture: String
    ): String? {
        val path = when (frameworkType) {
            FrameworkType.DYNAMIC -> "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry-Dynamic/Sentry-Dynamic.xcframework/$frameworkArchitecture"
            FrameworkType.STATIC -> "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry/Sentry.xcframework/$frameworkArchitecture"
        }
        return path.takeIf { File(it).exists() }
    }

    /**
     * Returns valid framework if exists with with the find command.
     * If the command finds multiple frameworks, it will return the one who has been modified or used most recently.
     */
    private fun findFrameworkWithFindCommand(
        frameworkType: FrameworkType,
        frameworkArchitecture: String
    ): String? {
        val output = ByteArrayOutputStream()

        val xcFrameworkName =
            if (frameworkType == FrameworkType.STATIC) "Sentry.xcframework" else "Sentry-Dynamic.xcframework"
        execOperations.exec {
            it.commandLine(
                "bash", "-c",
                "find \"${System.getProperty("user.home")}/Library/Developer/Xcode/DerivedData\" " +
                        "-name $xcFrameworkName " +
                        "-exec stat -f \"%m %N\" {} \\; | " +
                        "sort -nr | " +
                        "cut -d' ' -f2-"
            )
            it.standardOutput = output
            it.isIgnoreExitValue = true
        }

        val stringOutput = output.toString("UTF-8")
        val path = stringOutput.lineSequence()
            .firstOrNull()?.trim().orEmpty()
            .let { basePath ->
                "$basePath/$frameworkArchitecture"
            }
        return path.takeIf { File(it).exists() }
    }
}
