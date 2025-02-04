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
 * Handles the custom framework paths set by the user. This should generally be executed first.
 */
class CustomPathStrategy(
    private val project: Project,
) : FrameworkResolutionStrategy {
    private val linker: LinkerExtension = project.extensions.getByType(LinkerExtension::class.java)

    // In this function we don't really distinguish between static and dynamic framework
    // We trust that the user knows the distinction if they purposefully override the framework path
    override fun resolvePaths(architectures: Set<String>): FrameworkPaths {
        return linker.frameworkPath.orNull?.takeIf { it.isNotEmpty() }?.let { basePath ->
            when {
                basePath.endsWith("Sentry.xcframework") -> FrameworkPaths.createValidated(
                    staticBasePath = basePath,
                    architectures = architectures
                )

                basePath.endsWith("Sentry-Dynamic.xcframework") -> FrameworkPaths.createValidated(
                    dynamicBasePath = basePath,
                    architectures = architectures
                )

                else -> null
            }
        } ?: FrameworkPaths.NONE
    }
}

class DerivedDataStrategy(
    private val project: Project,
) : FrameworkResolutionStrategy {
    private val linker: LinkerExtension = project.extensions.getByType(LinkerExtension::class.java)

    override fun resolvePaths(architectures: Set<String>): FrameworkPaths {
        val xcodeprojPath = linker.xcodeprojPath.orNull
        val derivedDataPath = xcodeprojPath?.let { path ->
            project.providers.of(DerivedDataPathValueSource::class.java) {
                it.parameters.xcodeprojPath.set(path)
            }.get()
        } ?: return FrameworkPaths.NONE
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
}

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
        return strategies.firstNotNullOfOrNull { strategy ->
            try {
                strategy.resolvePaths(architectures)
            } catch (e: Exception) {
                project.logger.debug(
                    "Path resolution strategy ${strategy::class.simpleName} failed",
                    e
                )
                null
            }
        }
            ?: throw FrameworkLinkingException("All framework resolution strategies failed. Could not find Sentry Cocoa framework path")
    }

    companion object {
        /**
         * Resolution strategies for finding the framework path
         *
         * The order of resolution strategies matters, as the framework path will be resolved by the first successful strategy
         * Specifically here Custom Path will be checked first, if that fails then it is followed by the Derived Data strategy etc...
         */
        fun defaultStrategies(project: Project): List<FrameworkResolutionStrategy> {
            return listOf(
                CustomPathStrategy(project),
                DerivedDataStrategy(project),
                ManualSearchStrategy(project)
            )
        }
    }

}