package io.sentry.kotlin.multiplatform.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
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
            Arguments.of("latest")
        )
    }

    @ParameterizedTest(name = "Test SPM compatibility with Cocoa Version {0}")
    @MethodSource("cocoaVersions")
    fun `finds arch folders across different cocoa versions`(
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
        val frameworkDir = downloadAndUnzip(cocoaVersion)
        val xcFramework = File(frameworkDir, "Sentry.xcframework")

        val downloadedArchNames =
            xcFramework.listFiles()?.map { it.name } ?: throw IllegalStateException("No archs found")

        kmpExtension.appleTargets().forEach {
            val mappedArchNames = it.toSentryFrameworkArchitecture()
            val foundMatch = mappedArchNames.any { mappedArchName ->
                downloadedArchNames.contains(mappedArchName)
            }

            assert(foundMatch) {
                "Expected to find one of $mappedArchNames in $xcFramework for target ${it.name}.\nFound instead: ${xcFramework.listFiles()
                    ?.map { file -> file.name }}"
            }
        }
    }

    private fun downloadAndUnzip(cocoaVersion: String): File {
        val tempDir = Files.createTempDirectory("sentry-cocoa-test").toFile()
        tempDir.deleteOnExit()

        val targetFile = tempDir.resolve("Sentry.xcframework.zip")
        val downloadLink =
            if (cocoaVersion == "latest") "https://github.com/getsentry/sentry-cocoa/releases/latest/download/Sentry.xcframework.zip" else "https://github.com/getsentry/sentry-cocoa/releases/download/$cocoaVersion/Sentry.xcframework.zip"

        val url = URL(downloadLink)
        url.openStream().use { input ->
            Files.copy(
                input,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )
        }

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
