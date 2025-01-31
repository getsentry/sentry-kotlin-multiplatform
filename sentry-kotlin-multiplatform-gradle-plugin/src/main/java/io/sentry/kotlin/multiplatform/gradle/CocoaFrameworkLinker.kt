package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.konan.target.HostManager
import java.io.File

/**
 * Configures Sentry Cocoa framework linking for Apple targets in Kotlin Multiplatform projects.
 *
 * Resolves framework paths and applies necessary linker options to both test and framework binaries.
*/
class CocoaFrameworkLinker(
    private val project: Project,
    private val logger: Logger
) {
    private val pathResolver = FrameworkPathResolver(project)
    private val binaryLinker = FrameworkLinker(logger)

    fun configure(extension: LinkerExtension) {
        val kmpExtension =
            project.extensions.findByName("kotlin") as? KotlinMultiplatformExtension ?: run {
                logger.info("Skipping Apple framework linking: Kotlin Multiplatform extension not found")
                return
            }

        if (!HostManager.hostIsMac) {
            logger.info("Skipping Apple framework linking: Requires macOS host")
            return
        }

        kmpExtension.appleTargets().all { target ->
            try {
                processTarget(target, extension)
            } catch (e: Exception) {
                throw GradleException("Failed to configure ${target.name}: ${e.message}", e)
            }
        }
    }

    private fun processTarget(target: KotlinNativeTarget, linker: LinkerExtension) {
        val architectures =
            target.toSentryFrameworkArchitecture().takeIf { it.isNotEmpty() } ?: run {
                logger.warn("Skipping target ${target.name}: Unsupported architecture")
                return
            }

        val (dynamicPath, staticPath) = pathResolver.resolvePaths(linker, architectures)
        binaryLinker.configureBinaries(target.binaries, dynamicPath, staticPath)
    }
}

internal class FrameworkPathResolver(
    private val project: Project
) {
    fun resolvePaths(
        linker: LinkerExtension,
        architectures: Set<String>
    ): Pair<String?, String?> {
        val customPath = linker.frameworkPath.orNull?.takeIf { it.isNotEmpty() }
        val derivedData = linker.xcodeprojPath.orNull?.let(project::findDerivedDataPath)
            ?: project.findDerivedDataPath(null)

        return customPath?.let { path ->
            validateCustomPath(path)
            when {
                path.isStaticFrameworkPath() -> null to path
                path.isDynamicFrameworkPath() -> path to null
                else -> throw FrameworkLinkingException(
                    "Invalid framework name at $path - must be Sentry.xcframework or Sentry-Dynamic.xcframework"
                )
            }
        } ?: run {
            Pair(
                getFrameworkPath(FrameworkType.DYNAMIC, derivedData, architectures),
                getFrameworkPath(FrameworkType.STATIC, derivedData, architectures)
            )
        }
    }

    private fun validateCustomPath(path: String) {
        if (!File(path).exists()) {
            throw FrameworkLinkingException(
                "Custom framework path not found or does not exist: $path"
            )
        }

        if (path.isStaticFrameworkPath().not() && path.isDynamicFrameworkPath().not()) {
            throw FrameworkLinkingException("Invalid framework at $path - path must end with Sentry.xcframework or Sentry-Dynamic.xcframework")
        }
    }

    /**
     * Fallback method for fetching paths
     */
    private fun getFrameworkPath(
        type: FrameworkType,
        derivedData: String,
        architectures: Set<String>
    ) = project.providers.of(FrameworkPathValueSource::class.java) {
        it.parameters.frameworkType.set(type)
        it.parameters.derivedDataPath.set(derivedData)
        it.parameters.frameworkArchitectures.set(architectures)
    }.orNull

    private fun String.isStaticFrameworkPath() = endsWith("Sentry.xcframework")
    private fun String.isDynamicFrameworkPath() = endsWith("Sentry-Dynamic.xcframework")
}

internal class FrameworkLinker(
    private val logger: Logger
) {
    fun configureBinaries(binaries: KotlinNativeBinaryContainer, dynamicPath: String?, staticPath: String?) {
        validatePaths(dynamicPath, staticPath)

        binaries.all { binary ->
            when (binary) {
                is TestExecutable -> linkTestBinary(binary, chooseTestPath(dynamicPath, staticPath))
                is Framework -> linkFrameworkBinary(binary, dynamicPath, staticPath)
                else -> logger.info("Ignoring binary type: ${binary::class.java.simpleName}")
            }
        }
    }

    private fun chooseTestPath(dynamic: String?, static: String?) = when {
        dynamic != null && static != null -> {
            logger.debug("Both framework types available, preferring dynamic for tests")
            dynamic
        }

        dynamic != null -> dynamic
        static != null -> static
        else -> throw FrameworkLinkingException("No valid framework path found for tests")
    }

    private fun validatePaths(dynamic: String?, static: String?) {
        if (dynamic == null && static == null) {
            throw FrameworkLinkingException(frameworkNotFoundMessage)
        }
    }

    private fun linkTestBinary(binary: TestExecutable, path: String) {
        binary.linkerOpts("-rpath", path, "-F$path")
        logger.info("Linked Sentry Cocoa framework to test binary ${binary.name}")
    }

    private fun linkFrameworkBinary(binary: Framework, dynamicPath: String?, staticPath: String?) {
        val (path, type) = when {
            binary.isStatic && staticPath != null -> staticPath to "static"
            !binary.isStatic && dynamicPath != null -> dynamicPath to "dynamic"
            else -> throw FrameworkLinkingException(
                "Framework mismatch for ${binary.name}. " +
                        "Required ${if (binary.isStatic) "static" else "dynamic"} Sentry Cocoa framework not found."
            )
        }

        binary.linkerOpts("-F$path")
        logger.info("Linked $type Sentry Cocoa framework to ${binary.name}")
    }

    private val frameworkNotFoundMessage = """
        Failed to find Sentry Cocoa framework. Steps to resolve:
        
        1. Install Sentry Cocoa via SPM in Xcode
        2. Verify framework exists in Xcode's DerivedData folder:
           - Static: Sentry.xcframework
           - Dynamic: Sentry-Dynamic.xcframework
           
        If problem persists consider setting explicit path in build.gradle.kts:
        sentryKmp { 
            linker {
                frameworkPath.set("path/to/framework") 
            }
        }
        
        More details: https://docs.sentry.io/platforms/apple/install/
    """.trimIndent()
}

internal class FrameworkLinkingException(
    message: String,
    cause: Throwable? = null
) : GradleException(message, cause)

/**
 * Transforms a Kotlin Multiplatform target name to possible architecture names found inside
 * Sentry's framework directory.
 *
 * Returns a set of possible architecture names because Sentry Cocoa SDK has changed folder naming
 * across different versions. For example:
 * - iosArm64 -> ["ios-arm64", "ios-arm64_arm64e"]
 * - macosArm64 -> ["macos-arm64_x86_64", "macos-arm64_arm64e_x86_64"]
 * *
 * @return Set of possible architecture folder names for the given target. Returns empty set if target is not supported.
 */
internal fun KotlinNativeTarget.toSentryFrameworkArchitecture(): Set<String> = buildSet {
    when (name) {
        "iosSimulatorArm64", "iosX64" -> add("ios-arm64_x86_64-simulator")
        "iosArm64" -> {
            add("ios-arm64")
            add("ios-arm64_arm64e")
        }

        "macosArm64", "macosX64" -> {
            add("macos-arm64_x86_64")
            add("macos-arm64_arm64e_x86_64")
        }

        "tvosSimulatorArm64", "tvosX64" -> {
            add("tvos-arm64_x86_64-simulator")
            add("tvos-arm64_x86_64-simulator")
        }

        "tvosArm64" -> {
            add("tvos-arm64")
            add("tvos-arm64_arm64e")
        }

        "watchosArm32", "watchosArm64" -> {
            add("watchos-arm64_arm64_32_armv7k")
            add("watchos-arm64_arm64_32_arm64e_armv7k")
        }

        "watchosSimulatorArm64", "watchosX64" -> {
            add("watchos-arm64_i386_x86_64-simulator")
            add("watchos-arm64_i386_x86_64-simulator")
        }
    }
}

internal fun Project.findDerivedDataPath(customXcodeprojPath: String? = null): String {
    val xcodeprojPath = customXcodeprojPath ?: findXcodeprojFile(rootDir)?.absolutePath
    ?: throw GradleException("Xcode project file not found")

    return providers.of(DerivedDataPathValueSource::class.java) {
        it.parameters.xcodeprojPath.set(xcodeprojPath)
    }.get()
}

/**
 * Searches for a xcodeproj starting from the root directory. This function will only work for
 * monorepos and if it is not, the user needs to provide the custom path through the
 * [LinkerExtension] configuration.
 */
internal fun findXcodeprojFile(dir: File): File? {
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

internal fun KotlinMultiplatformExtension.appleTargets() =
    targets.withType(KotlinNativeTarget::class.java).matching {
        it.konanTarget.family.isAppleFamily
    }

enum class FrameworkType {
    STATIC,
    DYNAMIC
}