package io.sentry.kotlin.multiplatform.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

/**
 * Verifies that the Sentry Cocoa linking logic is **not** executed eagerly during the
 * configuration phase.  We create a project that contains both an Android (or JVM) and
 * an Apple target, configure the plugin and check that no linker options have been
 * added yet because no Apple-specific task is going to be executed.
 */
class CocoaFrameworkLinkerLazyExecutionTest {
    @Test
    fun `linking is deferred until an Apple task is present in the task graph`(@TempDir tmp: Path) {
        // === Project & plugins =============================================================
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("io.sentry.kotlin.multiplatform.gradle")
        }

        // === Targets ======================================================================
        val kmp = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmp.apply {
            // Non-Apple target so that we have at least one task we can build without iOS toolchains
            jvm()
        }

        // Create the Apple target and keep a reference to it so that we can inspect its binaries later
        val iosTarget = kmp.iosArm64()

        // Create a dummy framework binary so that, if the linker were executed prematurely,
        // it would mutate the linker options list which we later assert to be still empty.
        iosTarget.binaries.framework {
            baseName = "MyFramework"
            isStatic = true
        }

        // Set an obviously invalid framework path to ensure that – if the linker ran at this
        // point – it would fail with a FrameworkLinkingException.
        project.extensions.getByType(LinkerExtension::class.java).frameworkPath.set("/non/existing/path/Sentry.xcframework")

        // === Execute plugin configuration as if we were on macOS ===========================
        val plugin = project.plugins.getPlugin(SentryPlugin::class.java)
        plugin.executeConfiguration(project, hostIsMac = true)

        // === Assertion: The linkerOpts list of the framework binary must still be empty. ===
        val binary = iosTarget.binaries.first { it.baseName == "MyFramework" }
        assertTrue(binary.linkerOpts.isEmpty())
    }
}