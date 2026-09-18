package io.sentry.kotlin.multiplatform.gradle

import io.mockk.every
import io.mockk.mockk
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipFile

class SentryFrameworkArchitectureTest {
    companion object {
        @JvmStatic
        fun cocoaVersions(): List<Arguments> =
            listOf(
                Arguments.of("8.37.0"),
                Arguments.of("8.38.0"),
                Arguments.of("8.58.2"),
                Arguments.of("9.28.0"),
            )
    }

    @ParameterizedTest(name = "Test architecture name compatibility with Cocoa Version {0} in static framework")
    @MethodSource("cocoaVersions")
    fun `finds arch folders in static framework`(cocoaVersion: String) {
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
                watchosArm64(),
                watchosSimulatorArm64(),
                tvosX64(),
                tvosArm64(),
                tvosSimulatorArm64(),
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
            val foundMatch =
                mappedArchNames.any { mappedArchName ->
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
    fun `finds arch folders in dynamic framework`(cocoaVersion: String) {
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
                watchosArm64(),
                watchosSimulatorArm64(),
                tvosX64(),
                tvosArm64(),
                tvosSimulatorArm64(),
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
            val foundMatch =
                mappedArchNames.any { mappedArchName ->
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

    @ParameterizedTest
    @ValueSource(strings = ["watchosArm64", "watchosSimulatorArm64", "watchosX64"])
    fun `supported watchOS targets find Cocoa 9 slice names`(targetName: String) {
        val target = mockk<KotlinNativeTarget>()
        every { target.name } returns targetName
        val cocoa9Slice =
            if (targetName == "watchosArm64") {
                "watchos-arm64_arm64_32_arm64e"
            } else {
                "watchos-arm64_x86_64-simulator"
            }

        assertTrue(cocoa9Slice in target.toSentryFrameworkArchitecture())
    }

    @ParameterizedTest
    @ValueSource(strings = ["watchos-arm64_arm64_32", "watchos-arm64_arm64_32_arm64e"])
    fun `watchOS arm64 matches both Cocoa 9 device distributions`(slice: String) {
        val target = mockk<KotlinNativeTarget>()
        every { target.name } returns "watchosArm64"

        assertTrue(slice in target.toSentryFrameworkArchitecture())
    }

    @ParameterizedTest
    @ValueSource(strings = ["watchosArm32", "legacyWatch"])
    fun `stub watch target has no Cocoa architecture mapping`(targetName: String) {
        val target = mockk<KotlinNativeTarget>()
        every { target.name } returns targetName

        assertTrue(target.toSentryFrameworkArchitecture().isEmpty())
    }

    @Test
    fun `returns empty list if target is unsupported`() {
        val unsupportedTarget = mockk<KotlinNativeTarget>()
        every { unsupportedTarget.name } returns "unsupported"
        every { unsupportedTarget.konanTarget } returns
            mockk {
                every { family } returns
                    mockk {
                        every { isAppleFamily } returns true
                    }
            }

        assert(unsupportedTarget.toSentryFrameworkArchitecture().isEmpty()) {
            "Expected empty list for unsupported target"
        }
    }

    private fun downloadAndUnzip(
        cocoaVersion: String,
        isStatic: Boolean,
    ): File {
        // An optional local fixture directory permits offline checks against previously
        // downloaded release archives: <root>/<version>/Sentry[-Dynamic].xcframework.
        System.getenv("SENTRY_COCOA_TEST_FRAMEWORKS")?.let { root ->
            val directory = File(root, cocoaVersion)
            val framework = if (isStatic) "Sentry.xcframework" else "Sentry-Dynamic.xcframework"
            check(directory.resolve(framework).isDirectory) { "Missing framework fixture: $directory/$framework" }
            return directory
        }
        val tempDir = Files.createTempDirectory("sentry-cocoa-test").toFile()
        tempDir.deleteOnExit()

        val targetFile = tempDir.resolve("Framework.zip")

        // Download
        val xcFrameworkZip = if (isStatic) "Sentry.xcframework.zip" else "Sentry-Dynamic.xcframework.zip"
        val downloadLink =
            if (cocoaVersion ==
                "latest"
            ) {
                "https://github.com/getsentry/sentry-cocoa/releases/latest/download/$xcFrameworkZip"
            } else {
                "https://github.com/getsentry/sentry-cocoa/releases/download/$cocoaVersion/$xcFrameworkZip"
            }
        val url = URL(downloadLink)
        val connection =
            url.openConnection().apply {
                connectTimeout = 30_000
                readTimeout = 60_000
            }
        connection.getInputStream().use { input ->
            Files.copy(
                input,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
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
