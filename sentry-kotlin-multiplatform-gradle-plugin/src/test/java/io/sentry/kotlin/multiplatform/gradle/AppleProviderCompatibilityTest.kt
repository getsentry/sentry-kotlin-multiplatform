package io.sentry.kotlin.multiplatform.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading
import org.jetbrains.kotlin.konan.target.HostManager
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

class AppleProviderCompatibilityTest {
    @TempDir
    lateinit var dir: File

    @Test
    fun `AUTO installs nothing on Kotlin 2_2 and explicit SwiftPM fails`() {
        writeFixture(kotlinVersion = "2.2.21", provider = AppleDependencyProvider.AUTO)
        val result = runner().build()
        assertFalse(result.output.contains("Registered Sentry Cocoa"))

        writeFixture(kotlinVersion = "2.2.21", provider = AppleDependencyProvider.SWIFT_PM)
        val failure = runner().buildAndFail()
        assertTrue(failure.output.contains("SWIFT_PM is unavailable"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["2.4.0", "2.4.10", "2.4.20"])
    fun `AUTO registers Sentry Cocoa through official SwiftPM on Kotlin 2_4`(kotlinVersion: String) {
        writeFixture(kotlinVersion, AppleDependencyProvider.AUTO)
        val result = runner().build()
        if (HostManager.hostIsMac) {
            assertTrue(result.output.contains("Registered Sentry Cocoa"))
        }
    }

    @Test
    fun `official SwiftPM keeps higher minimums of transitive SwiftPM dependencies`() {
        assumeTrue(HostManager.hostIsMac)
        writeMultiModuleFixture()
        GradleRunner
            .create()
            .withProjectDir(dir)
            .withArguments(":app:generateSyntheticLinkageSwiftPMImportProjectForCinteropsAndLdDump", "--stacktrace")
            .build()
        val manifest = File(dir, "app/build/kotlin/swiftImport/Package.swift").readText()
        assertTrue(manifest.contains(".iOS(\"17.0\")"), manifest)
        assertTrue(manifest.contains(SENTRY_COCOA_GIT_URL), manifest)
    }

    private fun writeMultiModuleFixture() {
        File(dir, "settings.gradle.kts").writeText(
            """
            rootProject.name = "minimums-fixture"
            include(":lib", ":app")
            """.trimIndent(),
        )
        File(dir, "build.gradle.kts").writeText(
            """
            buildscript {
                repositories { mavenCentral(); gradlePluginPortal() }
                dependencies {
                    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.0")
                    classpath(files(${pluginClasspath()}))
                }
            }
            subprojects { repositories { mavenCentral() } }
            """.trimIndent(),
        )
        File(dir, "lib").mkdirs()
        File(dir, "lib/build.gradle.kts").writeText(
            """
            apply(plugin = "org.jetbrains.kotlin.multiplatform")
            configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
                iosSimulatorArm64()
                (this as ExtensionAware).extensions.configure<
                    org.jetbrains.kotlin.gradle.plugin.mpp.apple.swiftimport.SwiftPMImportExtension,
                >("swiftPMDependencies") {
                    iosMinimumDeploymentTarget.set("17.0")
                    swiftPackage(
                        url = url("https://github.com/apple/swift-collections.git"),
                        version = exact("1.1.4"),
                        products = listOf(product("DequeModule")),
                    )
                }
            }
            """.trimIndent(),
        )
        File(dir, "app").mkdirs()
        File(dir, "app/build.gradle.kts").writeText(
            """
            apply(plugin = "org.jetbrains.kotlin.multiplatform")
            apply(plugin = "io.sentry.kotlin.multiplatform.gradle")
            configure<io.sentry.kotlin.multiplatform.gradle.SentryExtension> {
                autoInstall.commonMain.enabled.set(false)
            }
            configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
                iosSimulatorArm64()
                sourceSets.getByName("commonMain").dependencies { implementation(project(":lib")) }
            }
            """.trimIndent(),
        )
    }

    private fun pluginClasspath(): String =
        PluginUnderTestMetadataReading
            .readImplementationClasspath()
            .joinToString(", ") { "\"${it.absolutePath.replace('\\', '/')}\"" }

    private fun runner(): GradleRunner =
        GradleRunner
            .create()
            .withProjectDir(dir)
            .withArguments("help", "--stacktrace")

    private fun writeFixture(
        kotlinVersion: String,
        provider: AppleDependencyProvider,
    ) {
        val classpath = pluginClasspath()
        File(dir, "settings.gradle.kts").writeText("rootProject.name = \"compatibility-fixture\"")
        File(dir, "build.gradle.kts").writeText(
            """
            import io.sentry.kotlin.multiplatform.gradle.AppleDependencyProvider
            buildscript {
                repositories { mavenCentral(); gradlePluginPortal() }
                dependencies {
                    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
                    classpath(files($classpath))
                }
            }
            apply(plugin = "org.jetbrains.kotlin.multiplatform")
            apply(plugin = "io.sentry.kotlin.multiplatform.gradle")
            configure<io.sentry.kotlin.multiplatform.gradle.SentryExtension> {
                autoInstall.commonMain.enabled.set(false)
                autoInstall.apple.provider.set(AppleDependencyProvider.$provider)
            }
            configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> { iosSimulatorArm64() }
            """.trimIndent(),
        )
    }
}
