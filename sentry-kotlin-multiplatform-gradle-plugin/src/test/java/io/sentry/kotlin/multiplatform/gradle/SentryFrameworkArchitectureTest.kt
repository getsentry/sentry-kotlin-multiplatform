package io.sentry.kotlin.multiplatform.gradle

import io.mockk.every
import io.mockk.mockk
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipFile

class SentryFrameworkArchitectureTest {
    companion object {
        @JvmStatic
        fun cocoaVersions(): List<Arguments> = listOf(
            Arguments.of("8.37.0"),
            Arguments.of("8.38.0"),
            Arguments.of("8.57.3")
//            Arguments.of("latest"),
            // TODO: Latest is already v9 which is currently failing - let's fix this when we bump to v9
        )
    }

    @ParameterizedTest(name = "Test architecture name compatibility with Cocoa Version {0} in static framework")
    @MethodSource("cocoaVersions")
    fun `finds arch folders in static framework`(
        cocoaVersion: String
    ) {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("io.sentry.kotlin.multiplatform.gradle")
        }

        val kmpExtension = project.extensions.getByName("kotlin") as KotlinMultiplatformExtension
        kmpExtension.apply {
            listOf(
                iosX64(),
                iosArm64(),
                iosSimulatorArm64(),
                macosArm64(),
                macosX64(),
                watchosX64(),
                watchosArm32(),
                watchosSimulatorArm64(),
                tvosX64(),
                tvosArm64(),
                tvosSimulatorArm64()
            ).forEach {
                it.binaries.framework {
                    baseName = "shared"
                    isStatic = false
                }
            }
        }
        val frameworkDir = downloadAndUnzip(cocoaVersion, isStatic = true)
        val xcFramework = File(frameworkDir, "Sentry.xcframework")

        val downloadedArchNames =
            xcFramework.listFiles()?.map { it.name } ?: throw IllegalStateException("No archs found")

        kmpExtension.appleTargets().forEach {
            val mappedArchNames = it.toSentryFrameworkArchitecture()
            val foundMatch = mappedArchNames.any { mappedArchName ->
                downloadedArchNames.contains(mappedArchName)
            }

            assert(foundMatch) {
                "Expected to find one of $mappedArchNames in $xcFramework for target ${it.name}.\nFound instead: ${
                xcFramework.listFiles()
                    ?.map { file -> file.name }
                }"
            }
        }
    }

    @ParameterizedTest(name = "Test architecture name compatibility with Cocoa Version {0} in dynamic framework")
    @MethodSource("cocoaVersions")
    fun `finds arch folders in dynamic framework`(
        cocoaVersion: String
    ) {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("io.sentry.kotlin.multiplatform.gradle")
        }

        val kmpExtension = project.extensions.getByName("kotlin") as KotlinMultiplatformExtension
        kmpExtension.apply {
            listOf(
                iosX64(),
                iosArm64(),
                iosSimulatorArm64(),
                macosArm64(),
                macosX64(),
                watchosX64(),
                watchosArm32(),
                watchosSimulatorArm64(),
                tvosX64(),
                tvosArm64(),
                tvosSimulatorArm64()
            ).forEach {
                it.binaries.framework {
                    baseName = "shared"
                    isStatic = false
                }
            }
        }
        val frameworkDir = downloadAndUnzip(cocoaVersion, isStatic = false)
        val xcFramework = File(frameworkDir, "Sentry-Dynamic.xcframework")

        val downloadedArchNames =
            xcFramework.listFiles()?.map { it.name } ?: throw IllegalStateException("No archs found")

        kmpExtension.appleTargets().forEach {
            val mappedArchNames = it.toSentryFrameworkArchitecture()
            val foundMatch = mappedArchNames.any { mappedArchName ->
                downloadedArchNames.contains(mappedArchName)
            }

            assert(foundMatch) {
                "Expected to find one of $mappedArchNames in $xcFramework for target ${it.name}.\nFound instead: ${
                xcFramework.listFiles()
                    ?.map { file -> file.name }
                }"
            }
        }
    }

    @Test
    fun `returns empty list if target is unsupported`() {
        val unsupportedTarget = mockk<KotlinNativeTarget>()
        every { unsupportedTarget.name } returns "unsupported"
        every { unsupportedTarget.konanTarget } returns mockk {
            every { family } returns mockk {
                every { isAppleFamily } returns true
            }
        }

        assert(unsupportedTarget.toSentryFrameworkArchitecture().isEmpty()) {
            "Expected empty list for unsupported target"
        }
    }

    private fun downloadAndUnzip(cocoaVersion: String, isStatic: Boolean): File {
        val tempDir = Files.createTempDirectory("sentry-cocoa-test").toFile()
        tempDir.deleteOnExit()

        val targetFile = tempDir.resolve("Framework.zip")

        // Download
        val xcFrameworkZip = if (isStatic) "Sentry.xcframework.zip" else "Sentry-Dynamic.xcframework.zip"
        val downloadLink =
            if (cocoaVersion == "latest") "https://github.com/getsentry/sentry-cocoa/releases/latest/download/$xcFrameworkZip" else "https://github.com/getsentry/sentry-cocoa/releases/download/$cocoaVersion/$xcFrameworkZip"
        val url = URL(downloadLink)
        url.openStream().use { input ->
            Files.copy(
                input,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        }

        // Unzip
        ZipFile(targetFile).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                val entryFile = File(tempDir, entry.name)
                if (entry.isDirectory) {
                    entryFile.mkdirs()
                } else {
                    entryFile.parentFile?.mkdirs()
                    zip.getInputStream(entry).use { input ->
                        entryFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }

        targetFile.delete()
        return tempDir
    }
}
