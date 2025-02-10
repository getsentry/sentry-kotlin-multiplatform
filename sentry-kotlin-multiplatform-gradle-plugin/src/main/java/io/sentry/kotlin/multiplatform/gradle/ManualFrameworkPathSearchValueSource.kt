package io.sentry.kotlin.multiplatform.gradle

import io.sentry.kotlin.multiplatform.gradle.SentryPlugin.Companion.logger
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream

abstract class ManualFrameworkPathSearchValueSource :
    ValueSource<String?, ManualFrameworkPathSearchValueSource.Parameters> {
    interface Parameters : ValueSourceParameters {
        val frameworkType: Property<FrameworkType>
        val basePathToSearch: Property<String>
    }

    @get:javax.inject.Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String? {
        val frameworkType = parameters.frameworkType.get()
        val basePathToSearch =
            parameters.basePathToSearch.orNull
                ?: "\"${System.getProperty("user.home")}/Library/Developer/Xcode/DerivedData\""
        return findFrameworkWithFindCommand(frameworkType, basePathToSearch)
    }

    /**
     * Returns valid framework if exists with the find command.
     * If the command finds multiple frameworks, it will return the one who has been modified or used most recently.
     */
    private fun findFrameworkWithFindCommand(
        frameworkType: FrameworkType,
        basePathToSearch: String
    ): String? {
        val stdOutput = ByteArrayOutputStream()
        val errOutput = ByteArrayOutputStream()

        val xcFrameworkName =
            if (frameworkType == FrameworkType.STATIC) "Sentry.xcframework" else "Sentry-Dynamic.xcframework"
        val execResult = execOperations.exec {
            it.commandLine(
                "bash",
                "-c",
                "find $basePathToSearch " +
                    "-name $xcFrameworkName " +
                    "-exec stat -f \"%m %N\" {} \\; | " +
                    "sort -nr | " +
                    "cut -d' ' -f2-"
            )
            it.standardOutput = stdOutput
            it.errorOutput = errOutput
        }

        val stringOutput = stdOutput.toString("UTF-8")
        return if (execResult.exitValue == 0) {
            if (stringOutput.lineSequence().firstOrNull().isNullOrEmpty()) {
                null
            } else {
                stringOutput.lineSequence()
                    .first()
            }
        } else {
            logger.warn(
                "Manual search failed to find $xcFrameworkName in $basePathToSearch. " +
                    "Error output: ${errOutput.toString(Charsets.UTF_8)}"
            )
            null
        }
    }
}
