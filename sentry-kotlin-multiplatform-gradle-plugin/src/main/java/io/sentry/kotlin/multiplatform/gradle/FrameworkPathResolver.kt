package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.Project
import java.io.File

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
            // Find first valid dynamic path
            val dynamicPath = dynamicBasePath?.let { basePath ->
                architectures.firstNotNullOfOrNull { arch ->
                    val path = "$basePath/$arch"
                    path.takeIf { pathExists(it) }
                }
            }

            // Find first valid static path
            val staticPath = staticBasePath?.let { basePath ->
                architectures.firstNotNullOfOrNull { arch ->
                    val path = "$basePath/$arch"
                    path.takeIf { pathExists(it) }
                }
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

sealed interface FrameworkResolutionStrategy {
    fun resolvePaths(
        architectures: Set<String>,
    ): FrameworkPaths
}

/**
 * Finds the framework path based on the custom framework paths set by the user. This should generally be executed first.
 */
class CustomPathStrategy(
    private val project: Project,
) : FrameworkResolutionStrategy {
    private val linker: LinkerExtension = project.extensions.getByType(LinkerExtension::class.java)

    // In this function we don't really distinguish between static and dynamic framework
    // We trust that the user knows the distinction if they purposefully override the framework path
    override fun resolvePaths(architectures: Set<String>): FrameworkPaths {
        return linker.frameworkPath.orNull?.takeIf { it.isNotEmpty() }?.let { basePath ->
            project.logger.lifecycle(
                "Resolving Sentry Cocoa framework paths using custom path: {} and looking for Sentry architectures: {} within the custom path",
                basePath,
                architectures
            )

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
) : FrameworkResolutionStrategy {
    private val linker: LinkerExtension = project.extensions.getByType(LinkerExtension::class.java)

    override fun resolvePaths(architectures: Set<String>): FrameworkPaths {
        project.logger.lifecycle("Resolving Sentry Cocoa framework paths using derived data path")

        val xcodeprojSetByUser = linker.xcodeprojPath.orNull?.takeIf { it.isNotEmpty() }
        val foundXcodeproj = xcodeprojSetByUser ?: findXcodeprojFile2(project.rootDir)?.absolutePath

        val derivedDataPath = foundXcodeproj?.let { path ->
            project.providers.of(DerivedDataPathValueSource::class.java) {
                it.parameters.xcodeprojPath.set(path)
            }.orNull
        } ?: FrameworkPaths.NONE

        val dynamicBasePath =
            "${derivedDataPath}/SourcePackages/artifacts/sentry-cocoa/Sentry-Dynamic/Sentry-Dynamic.xcframework"
        val staticBasePath =
            "${derivedDataPath}/SourcePackages/artifacts/sentry-cocoa/Sentry/Sentry.xcframework"

        return FrameworkPaths.createValidated(
            dynamicBasePath = dynamicBasePath,
            staticBasePath = staticBasePath,
            architectures = architectures
        )
    }

    /**
     * Searches for a xcodeproj starting from the root directory. This function will only work for
     * monorepos and if it is not, the user needs to provide the custom path through the
     * [LinkerExtension] configuration.
     */
    internal fun findXcodeprojFile2(dir: File): File? {
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

        return searchDirectory(dir)
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
) : FrameworkResolutionStrategy {
    override fun resolvePaths(architectures: Set<String>): FrameworkPaths {
        val dynamicValueSource =
            project.providers.of(ManualFrameworkPathSearchValueSource::class.java) {
                it.parameters.frameworkType.set(FrameworkType.DYNAMIC)
                it.parameters.frameworkArchitectures.set(architectures)
            }
        val staticValueSource =
            project.providers.of(ManualFrameworkPathSearchValueSource::class.java) {
                it.parameters.frameworkType.set(FrameworkType.STATIC)
                it.parameters.frameworkArchitectures.set(architectures)
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
    private val strategies: List<FrameworkResolutionStrategy> = defaultStrategies(project),
) {
    fun resolvePaths(
        architectures: Set<String>
    ): FrameworkPaths {
        return strategies.asSequence()
            .mapNotNull { strategy ->
                try {
                    val result = strategy.resolvePaths(architectures)
                    when {
                        result == FrameworkPaths.NONE -> {
                            project.logger.debug("Strategy ${strategy::class.simpleName} returned no paths")
                            null
                        }

                        else -> {
                            project.logger.lifecycle("✅ Found Sentry Cocoa framework paths using ${strategy::class.simpleName}")
                            result
                        }
                    }
                } catch (e: Exception) {
                    project.logger.debug(
                        "Strategy ${strategy::class.simpleName} failed: ${e.message}"
                    )
                    null
                }
            }
            .firstOrNull()
            ?: throw FrameworkLinkingException(
                "No valid framework paths found. Attempted strategies: ${
                    strategies.joinToString { it::class.simpleName!! }
                }"
            )
    }

    companion object {
        /**
         * Default resolution strategies for finding the Sentry Cocoa framework path.
         *
         * The order of resolution strategies matters, as the framework path will be resolved by the first successful strategy
         * Specifically here Custom Path will be checked first, if that fails then it is followed by the Derived Data strategy etc...
         */
        fun defaultStrategies(project: Project): List<FrameworkResolutionStrategy> {
            return listOf(
                CustomPathStrategy(project),
                DerivedDataStrategy(project),
                ManualSearchStrategy(project),
                // TODO: add DownloadStrategy -> downloads the framework and stores it in build dir
                // this is especially useful for users who dont have a monorepo setup
            )
        }
    }
}