package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.Project
import java.io.File

enum class FrameworkType {
    STATIC,
    DYNAMIC
}

data class FrameworkPaths(
    val dynamic: String? = null,
    val static: String? = null
) {
    companion object {
        val NONE = FrameworkPaths(null, null)

        fun createValidated(
            dynamicBasePath: String? = null,
            staticBasePath: String? = null,
            architectures: Set<String>,
            pathExists: (String) -> Boolean = { path -> File(path).exists() }
        ): FrameworkPaths {
            val dynamicPath = dynamicBasePath?.let { basePath ->
                architectures.map { arch -> "$basePath/$arch" }.firstOrNull { pathExists(it) }
            }

            val staticPath = staticBasePath?.let { basePath ->
                architectures.map { arch -> "$basePath/$arch" }.firstOrNull { pathExists(it) }
            }

            return when {
                dynamicPath != null && staticPath != null ->
                    FrameworkPaths(dynamic = dynamicPath, static = staticPath)

                dynamicPath != null ->
                    FrameworkPaths(dynamic = dynamicPath)

                staticPath != null ->
                    FrameworkPaths(static = staticPath)

                else ->
                    NONE
            }
        }
    }
}

interface FrameworkResolutionStrategy {
    fun resolvePaths(architectures: Set<String>): FrameworkPaths
}

/**
 * Finds the framework path based on the custom framework paths set by the user.
 * This should generally be executed first.
 */
class CustomPathStrategy(
    private val project: Project
) : FrameworkResolutionStrategy {
    private val linker: LinkerExtension = project.extensions.getByType(LinkerExtension::class.java)

    // In this function we don't distinguish between static and dynamic frameworks
    // We trust that the user knows the distinction if they purposefully override the framework path
    override fun resolvePaths(architectures: Set<String>): FrameworkPaths {
        val result = linker.frameworkPath.orNull?.takeIf { it.isNotEmpty() }?.let { basePath ->
            when {
                basePath.endsWith("Sentry.xcframework") -> FrameworkPaths.createValidated(
                    staticBasePath = basePath,
                    architectures = architectures
                )

                basePath.endsWith("Sentry-Dynamic.xcframework") -> FrameworkPaths.createValidated(
                    dynamicBasePath = basePath,
                    architectures = architectures
                )

                else -> FrameworkPaths.NONE
            }
        } ?: FrameworkPaths.NONE
        if (linker.frameworkPath.orNull != null && result == FrameworkPaths.NONE) {
            project.logger.warn(
                "Custom framework path has been set manually but could not be found. " +
                    "Trying to resolve framework paths using other strategies."
            )
        }
        return result
    }
}

/**
 * Finds framework paths based on the derived data path.
 *
 * This strategy prioritizes:
 * 1. A user-specified Xcode project path via [LinkerExtension].
 * 2. An auto-found Xcode project in the root directory. (mainly works for mono repo)
 */
class DerivedDataStrategy(
    private val project: Project,
    private val derivedDataProvider: (String) -> String? = { xcodeprojPath ->
        project.providers.of(DerivedDataPathValueSource::class.java) {
            it.parameters.xcodeprojPath.set(xcodeprojPath)
        }.orNull
    }
) : FrameworkResolutionStrategy {
    private val linker: LinkerExtension = project.extensions.getByType(LinkerExtension::class.java)

    override fun resolvePaths(architectures: Set<String>): FrameworkPaths {
        val xcodeprojSetByUser = linker.xcodeprojPath.orNull?.takeIf { it.isNotEmpty() }
        val foundXcodeproj = xcodeprojSetByUser ?: findXcodeprojFile(project.rootDir)?.absolutePath
        if (foundXcodeproj == null) {
            return FrameworkPaths.NONE
        }

        val derivedDataPath = derivedDataProvider(foundXcodeproj) ?: return FrameworkPaths.NONE
        val dynamicBasePath =
            "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry-Dynamic/Sentry-Dynamic.xcframework"
        val staticBasePath =
            "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry/Sentry.xcframework"

        return FrameworkPaths.createValidated(
            dynamicBasePath = dynamicBasePath,
            staticBasePath = staticBasePath,
            architectures = architectures
        )
    }

    /**
     * Searches for a xcodeproj starting from the root directory. This function will only work for
     * monorepos and if it is not, the user needs to provide the [LinkerExtension.xcodeprojPath].
     */
    private fun findXcodeprojFile(startingDir: File): File? {
        val ignoredDirectories = listOf("build", "DerivedData")

        fun searchDirectory(directory: File): File? {
            val files = directory.listFiles() ?: return null

            return files.firstNotNullOfOrNull { file ->
                when {
                    file.name in ignoredDirectories -> null
                    file.extension == "xcodeproj" -> file
                    file.isDirectory -> searchDirectory(file)
                    else -> null
                }
            }
        }

        return searchDirectory(startingDir)
    }
}

/**
 * Performs a manual search for Sentry Cocoa frameworks using system tools.
 *
 * This strategy:
 * - Searches the DerivedData for valid framework paths
 * - Returns first validated paths found for either static or dynamic frameworks
 *
 * If multiple paths were found for a single framework type, the most recently used is chosen.
 * See [ManualFrameworkPathSearchValueSource] for details.
 */
class ManualSearchStrategy(
    private val project: Project,
    private val basePathToSearch: String? = null
) : FrameworkResolutionStrategy {
    // TODO: currently the search doesnt differentiate between Cocoa versions
    // we can improve this by checking the info.plist and prefer the ones that are the version we are looking for
    override fun resolvePaths(architectures: Set<String>): FrameworkPaths {
        val dynamicValueSource =
            project.providers.of(ManualFrameworkPathSearchValueSource::class.java) {
                it.parameters.frameworkType.set(FrameworkType.DYNAMIC)
                if (basePathToSearch != null) {
                    it.parameters.basePathToSearch.set(basePathToSearch)
                }
            }
        val staticValueSource =
            project.providers.of(ManualFrameworkPathSearchValueSource::class.java) {
                it.parameters.frameworkType.set(FrameworkType.STATIC)
                if (basePathToSearch != null) {
                    it.parameters.basePathToSearch.set(basePathToSearch)
                }
            }

        return FrameworkPaths.createValidated(
            dynamicBasePath = dynamicValueSource.orNull,
            staticBasePath = staticValueSource.orNull,
            architectures = architectures
        )
    }
}

class FrameworkPathResolver(
    private val project: Project,
    private val strategies: List<FrameworkResolutionStrategy> = defaultStrategies(project)
) {
    fun resolvePaths(
        architectures: Set<String>
    ): FrameworkPaths {
        strategies.forEach { strategy ->
            try {
                project.logger.info(
                    "Attempt to resolve Sentry Cocoa framework paths using ${strategy::class.simpleName}"
                )
                val result = strategy.resolvePaths(architectures)
                if (result != FrameworkPaths.NONE) {
                    val path = result.dynamic ?: result.static
                    project.logger.lifecycle("Found Sentry Cocoa framework path using ${strategy::class.simpleName} at $path")
                    return result
                } else {
                    project.logger.debug("Strategy ${strategy::class.simpleName} did not find valid paths")
                }
            } catch (e: FrameworkLinkingException) {
                project.logger.warn(
                    "Strategy ${strategy::class.simpleName} failed due to error: ${e.message}"
                )
            }
        }

        // If at this point we didn't find a path to the framework, we cannot proceed
        throw FrameworkLinkingException(frameworkNotFoundMessage)
    }

    private val frameworkNotFoundMessage = """
        Failed to find Sentry Cocoa framework. Steps to resolve:
        
        1. Install Sentry Cocoa via SPM in Xcode
        2. Verify framework exists in Xcode's DerivedData folder:
           - If static: Sentry.xcframework
           - If dynamic: Sentry-Dynamic.xcframework
           
        If problem persists consider setting explicit path in build.gradle.kts:
        sentryKmp { 
            linker {
                frameworkPath.set("path/to/Sentry.xcframework") 
            }
        }
    """.trimIndent()

    companion object {
        /**
         * Default resolution strategies for finding the Sentry Cocoa framework path.
         *
         * The order of resolution strategies matters, as the framework path will be
         * resolved by the first successful strategy. Specifically here Custom Path will be checked first,
         * if that fails then it is followed by the Derived Data strategy etc...
         */
        fun defaultStrategies(project: Project): List<FrameworkResolutionStrategy> {
            return listOf(
                CustomPathStrategy(project),
                DerivedDataStrategy(project),
                ManualSearchStrategy(project)
                // TODO: add DownloadStrategy -> downloads the framework and stores it in build dir
                // this is especially useful for users who dont have a monorepo setup
            )
        }
    }
}
