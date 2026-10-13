import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/** Embeds the bridge's requested Swift compatibility libraries before cinterop packages it. */
@DisableCachingByDefault(because = "The generated cinterop definition contains absolute build paths")
abstract class BundleSwiftCompatibility : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val bridgeDefinition: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val bridgeArchive: RegularFileProperty

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val compatibilityLibraries: ConfigurableFileCollection

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val libtool: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val otool: RegularFileProperty

    @get:Input
    abstract val architecture: Property<String>

    @get:OutputFile
    abstract val bundledArchive: RegularFileProperty

    @get:OutputFile
    abstract val bundledDefinition: RegularFileProperty

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun bundle() {
        val loadCommands = ByteArrayOutputStream()
        execOperations.exec {
            commandLine(otool.get().asFile, "-l", bridgeArchive.get().asFile)
            standardOutput = loadCommands
        }
        val requiredLibraries =
            Regex("string #\\d+ -l(swiftCompatibility\\S+)")
                .findAll(loadCommands.toString())
                .map { "lib${it.groupValues[1]}.a" }
                .toSortedSet()
        val availableLibraries = compatibilityLibraries.files.associateBy { it.name }
        val archives =
            requiredLibraries.map { name ->
                availableLibraries[name]
                    ?: throw GradleException("Missing Swift compatibility archive $name for ${architecture.get()}")
            }
        val output = bundledArchive.get().asFile
        output.parentFile.mkdirs()
        execOperations.exec {
            commandLine(
                listOf(
                    libtool.get().asFile.absolutePath,
                    "-static",
                    "-arch_only",
                    architecture.get(),
                    "-o",
                    output.absolutePath,
                    bridgeArchive.get().asFile.absolutePath,
                ) + archives.map { it.absolutePath },
            )
            environment("ZERO_AR_DATE", "1")
        }

        val originalDefinition = bridgeDefinition.get().asFile.readText()
        val libraryPaths = Regex("(?m)^libraryPaths\\s*=.*$")
        val staticLibraries = Regex("(?m)^staticLibraries\\s*=.*$")
        check(libraryPaths.containsMatchIn(originalDefinition) && staticLibraries.containsMatchIn(originalDefinition)) {
            "The Swift bridge definition must declare libraryPaths and staticLibraries"
        }
        bundledDefinition.get().asFile.writeText(
            originalDefinition
                .replace(libraryPaths) { "libraryPaths = \"${output.parentFile.absolutePath}\"" }
                .replace(staticLibraries) { "staticLibraries = ${output.name}" },
        )
        logger.lifecycle("Bundled Swift compatibility for {}: {}", name, requiredLibraries.joinToString())
    }
}
