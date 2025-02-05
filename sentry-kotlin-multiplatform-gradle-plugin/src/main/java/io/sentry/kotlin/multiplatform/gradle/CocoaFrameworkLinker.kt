package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import java.awt.Frame

/**
 * Configures Sentry Cocoa framework linking for Apple targets in Kotlin Multiplatform projects.
 *
 * Resolves framework paths and applies necessary linker options to both test and framework binaries.
 */
class CocoaFrameworkLinker(
    private val logger: Logger,
    private val pathResolver: FrameworkPathResolver,
    private val binaryLinker: FrameworkLinker,
    private val hostIsMac: Boolean
) {
    fun configure(
        appleTargets: List<KotlinNativeTarget>,
    ) {
        if (!hostIsMac) {
            throw FrameworkLinkingException("Sentry Cocoa framework linking requires a macOS host")
        }

        appleTargets.forEach { target ->
            try {
                logger.lifecycle(
                    "Start resolving Sentry Cocoa framework paths for target: ${target.name}"
                )
                processTarget(target)
            } catch (e: Exception) {
                throw FrameworkLinkingException("Failed to configure ${target.name}: ${e.message}", e)
            }
        }
    }

    private fun processTarget(target: KotlinNativeTarget) {
        val architectures =
            target.toSentryFrameworkArchitecture().takeIf { it.isNotEmpty() } ?: run {
                logger.warn("Skipping target ${target.name}: Unsupported architecture")
                return
            }
        val paths: FrameworkPaths = architectures.firstNotNullOf { arch ->
            pathResolver.resolvePaths(arch).takeIf { it != FrameworkPaths.NONE }
        }
        binaryLinker.configureBinaries(target.binaries, paths.dynamic, paths.static)
    }
}

class FrameworkLinker(
    private val logger: Logger
) {
    fun configureBinaries(
        binaries: KotlinNativeBinaryContainer,
        dynamicPath: String?,
        staticPath: String?
    ) {
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

    private fun linkTestBinary(binary: TestExecutable, path: String) {
        // Linking in test binaries works with both dynamic and static framework
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
 * - iosArm64 -> [SentryCocoaFrameworkArchitectures.IOS_ARM64]
 * - macosArm64 -> [SentryCocoaFrameworkArchitectures.MACOS_ARM64_AND_X64]
 * 
 * @return Set of possible architecture folder names for the given target. Returns empty set if target is not supported.
 */
internal fun KotlinNativeTarget.toSentryFrameworkArchitecture(): Set<String> = buildSet {
    when (name) {
        "iosSimulatorArm64", "iosX64" -> addAll(SentryCocoaFrameworkArchitectures.IOS_SIMULATOR_AND_X64)
        "iosArm64" -> addAll(SentryCocoaFrameworkArchitectures.IOS_ARM64)
        "macosArm64", "macosX64" -> addAll(SentryCocoaFrameworkArchitectures.MACOS_ARM64_AND_X64)
        "tvosSimulatorArm64", "tvosX64" -> addAll(SentryCocoaFrameworkArchitectures.TVOS_SIMULATOR_AND_X64)
        "tvosArm64" -> addAll(SentryCocoaFrameworkArchitectures.TVOS_ARM64)
        "watchosArm32", "watchosArm64" -> addAll(SentryCocoaFrameworkArchitectures.WATCHOS_ARM)
        "watchosSimulatorArm64", "watchosX64" -> addAll(SentryCocoaFrameworkArchitectures.WATCHOS_SIMULATOR_AND_X64)
    }
}

internal object SentryCocoaFrameworkArchitectures {
    val IOS_SIMULATOR_AND_X64 = setOf("ios-arm64_x86_64-simulator")
    val IOS_ARM64 = setOf("ios-arm64", "ios-arm64_arm64e")
    val MACOS_ARM64_AND_X64 = setOf("macos-arm64_x86_64", "macos-arm64_arm64e_x86_64")
    val TVOS_SIMULATOR_AND_X64 = setOf("tvos-arm64_x86_64-simulator")
    val TVOS_ARM64 = setOf("tvos-arm64", "tvos-arm64_arm64e")
    val WATCHOS_ARM = setOf("watchos-arm64_arm64_32_armv7k", "watchos-arm64_arm64_32_arm64e_armv7k")
    val WATCHOS_SIMULATOR_AND_X64 = setOf("watchos-arm64_i386_x86_64-simulator")

    // Used for tests
    val all = setOf(IOS_SIMULATOR_AND_X64, IOS_ARM64, MACOS_ARM64_AND_X64, TVOS_SIMULATOR_AND_X64, TVOS_ARM64, WATCHOS_ARM, WATCHOS_SIMULATOR_AND_X64)
}

internal fun KotlinMultiplatformExtension.appleTargets() =
    targets.withType(KotlinNativeTarget::class.java).matching {
        it.konanTarget.family.isAppleFamily
    }
