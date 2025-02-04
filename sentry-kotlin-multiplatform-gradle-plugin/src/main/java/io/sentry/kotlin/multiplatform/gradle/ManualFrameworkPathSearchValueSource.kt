package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream

abstract class ManualFrameworkPathSearchValueSource : ValueSource<String?, ManualFrameworkPathSearchValueSource.Parameters> {
    interface Parameters : ValueSourceParameters {
        val frameworkArchitectures: SetProperty<String>
        val frameworkType: Property<FrameworkType>
    }

    @get:javax.inject.Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String? {
        val frameworkArchitectures = parameters.frameworkArchitectures.get()
        val frameworkType = parameters.frameworkType.get()

        return frameworkArchitectures.firstNotNullOfOrNull { architecture ->
            findFrameworkWithFindCommand(frameworkType, architecture)
        }
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
        return stringOutput.lineSequence().firstOrNull()
    }
}
