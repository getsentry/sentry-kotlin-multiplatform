package io.sentry.kotlin.multiplatform.gradle

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading
import org.gradle.testkit.runner.internal.io.SynchronizedOutputStream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStreamWriter

@EnabledOnOs(OS.MAC)
class CocoaFrameworkLinkerIntegrationTest {
    /**
     * Verifies that the Cocoa linker is **not** configured when the task graph
     * contains only non-Apple targets.
     */
    @Test
    fun `linker is not configured when only non-Apple tasks are requested`(@TempDir projectDir: File) {
        writeBuildFiles(projectDir)

        val output = ByteArrayOutputStream()
        defaultRunner(projectDir, output)
            .withArguments("compileKotlinJvm", "--dry-run", "--info")
            .build()

        assertThat(output.toString())
            .contains("No Apple compile task scheduled for this build - skipping Sentry Cocoa framework linking")
    }

    /**
     * Verifies that the Cocoa linker **is** configured when at least one Apple
     * task is present in the task graph.
     */
    @Test
    fun `linker is configured when an Apple task is requested`(@TempDir projectDir: File) {
        writeBuildFiles(projectDir)

        val output = ByteArrayOutputStream()
        defaultRunner(projectDir, output)
            .withArguments("compileKotlinIosSimulatorArm64", "--dry-run", "--info")
            .build()

        assertThat(output.toString())
            .contains("Set up Sentry Cocoa linking for targets: [iosSimulatorArm64]")
        assertThat(output.toString())
            .contains("Start resolving Sentry Cocoa framework paths for target: iosSimulatorArm64")
    }

    // ---------------------------------------------------------------------
    // test-fixture helpers
    // ---------------------------------------------------------------------

    private fun writeBuildFiles(dir: File) {
        // -----------------------------------------------------------------
        // Create a fake XCFramework on disk so that the CustomPathStrategy
        // can resolve a valid framework path even on CI machines where SPM
        // (and hence DerivedData) is not available.
        // -----------------------------------------------------------------
        val fakeFrameworkDir = File(dir, "Sentry-Dynamic.xcframework").apply {
            // Create minimal structure that satisfies path validation logic
            val archDirName = "ios-arm64_x86_64-simulator" // architecture used for iosSimulatorArm64
            val archDir = File(this, archDirName)
            archDir.mkdirs()
        }

        File(dir, "settings.gradle").writeText("""rootProject.name = "fixture"""")

        val pluginClasspath = PluginUnderTestMetadataReading
            .readImplementationClasspath()
            .joinToString(", ") { "\"${it.absolutePath.replace('\\', '/')}\"" }

        File(dir, "build.gradle").writeText(
            """
            buildscript {
              repositories {
                google()
                mavenCentral()
                gradlePluginPortal()
              }
              dependencies {
                classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION"
                classpath files($pluginClasspath)
              }
            }

            apply plugin: 'org.jetbrains.kotlin.multiplatform'
            apply plugin: 'io.sentry.kotlin.multiplatform.gradle'

            repositories {
              google()
              mavenCentral()
            }

            kotlin {
              jvm()                // non-Apple target
              iosSimulatorArm64()   // Apple target used in tests
            }
            
            // -----------------------------------------------------------------
            // Configure the plugin to use the fake framework path created above.
            // This makes the CustomPathStrategy succeed immediately, bypassing the
            // DerivedData and manual search strategies that rely on an SPM setup.
            // -----------------------------------------------------------------
            sentryKmp {
              linker {
                frameworkPath.set("${fakeFrameworkDir.absolutePath.replace('\\', '/')}")
              }
            }
            """.trimIndent()
        )
    }

    /** Returns a pre-configured [GradleRunner] that logs into [out]. */
    private fun defaultRunner(projectDir: File, out: ByteArrayOutputStream): GradleRunner =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withGradleVersion(org.gradle.util.GradleVersion.current().version)
            .forwardStdOutput(OutputStreamWriter(SynchronizedOutputStream(out)))
            .forwardStdError(OutputStreamWriter(SynchronizedOutputStream(out)))
            .withArguments("--stacktrace")

    private companion object {
        private const val KOTLIN_VERSION = "2.1.21"
    }
}
