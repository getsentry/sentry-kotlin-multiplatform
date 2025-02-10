package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.logging.Logger
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

/**
 * Responsible for executing the linking.
 * This involves configuring and linking binaries to the Sentry Cocoa framework.
 */
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
