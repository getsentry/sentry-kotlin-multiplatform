package io.sentry.kotlin.multiplatform.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

class AppleProviderCompatibilityTest {
    @ParameterizedTest
    @ValueSource(strings = ["2.2.21", "2.4.0", "2.4.10"])
    fun `plugin DSL and provider adapter work with consumer Kotlin versions`(
        version: String,
        @TempDir dir: File,
    ) {
        val classpath =
            PluginUnderTestMetadataReading
                .readImplementationClasspath()
                .joinToString(", ") { "\"${it.absolutePath.replace('\\', '/')}\"" }
        File(dir, "settings.gradle.kts").writeText("rootProject.name = \"compatibility-fixture\"")
        val official = version != "2.2.21"
        File(dir, "build.gradle.kts").writeText(
            """
            import io.sentry.kotlin.multiplatform.gradle.AppleDependencyProvider
            buildscript {
                repositories { mavenCentral(); gradlePluginPortal() }
                dependencies {
                    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$version")
                    classpath(files($classpath))
                }
            }
            apply(plugin = "org.jetbrains.kotlin.multiplatform")
            apply(plugin = "io.sentry.kotlin.multiplatform.gradle")
            configure<io.sentry.kotlin.multiplatform.gradle.SentryExtension> {
                autoInstall.commonMain.enabled.set(false)
                autoInstall.apple.provider.set(AppleDependencyProvider.${if (official) "SWIFT_PM" else "NONE"})
            }
            configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> { iosSimulatorArm64() }
            """.trimIndent(),
        )
        val result =
            GradleRunner
                .create()
                .withProjectDir(dir)
                .withArguments("help", "--stacktrace")
                .build()
        assertTrue(result.output.contains("BUILD SUCCESSFUL"))
        if (!official) {
            val build = File(dir, "build.gradle.kts")
            build.writeText(build.readText().replace("AppleDependencyProvider.NONE", "AppleDependencyProvider.SWIFT_PM"))
            val failure =
                GradleRunner
                    .create()
                    .withProjectDir(dir)
                    .withArguments("help")
                    .buildAndFail()
            assertTrue(failure.output.contains("SWIFT_PM is unavailable"))
        }
        if (official && org.jetbrains.kotlin.konan.target.HostManager.hostIsMac) {
            assertTrue(result.output.contains("Registered Sentry Cocoa"))
        }
    }
}
