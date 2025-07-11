package io.sentry.kotlin.multiplatform.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

/**
 * Ensures that the Cocoa framework linking logic *does* run (and therefore modifies
 * the framework binary) when we manually trigger it for a build that contains an
 * Apple target and a resolvable XCFramework path.
 */
class CocoaFrameworkLinkerRunsWhenAppleTaskPresentTest {
    @Test
    fun `linking is executed when Apple target tasks are requested`(@TempDir tmp: Path) {
        // === Prepare a fake XCFramework on disk ============================================
        val xcframeworkBase = tmp.resolve("Sentry.xcframework")
        val archDir = Files.createDirectories(xcframeworkBase.resolve("ios-arm64"))

        // === Project & plugins =============================================================
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("io.sentry.kotlin.multiplatform.gradle")
        }

        // === Targets ======================================================================
        val kmp = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmp.apply {
            // Non-Apple target so the project is realistically multi-platform
            jvm()
            // Apple target will be added explicitly below
        }

        val iosTarget = kmp.iosArm64()
        iosTarget.binaries.framework {
            baseName = "MyFramework"
            isStatic = true
        }

        // Point the linker extension at our fake framework
        project.extensions.getByType(LinkerExtension::class.java).frameworkPath.set(xcframeworkBase.absolutePathString())

        // === Manually invoke the linking logic =============================================
        val linker = CocoaFrameworkLinker(
            logger = project.logger,
            pathResolver = FrameworkPathResolver(project),
            binaryLinker = FrameworkLinker(project.logger)
        )
        linker.configure(kmp.appleTargets().toList())

        // === Verify that linker options have been added to the framework binary ============
        val binary = iosTarget.binaries.first { it.baseName == "MyFramework" }
        assertTrue(binary.linkerOpts.isNotEmpty())
        assertEquals("-F${xcframeworkBase.absolutePathString()}", binary.linkerOpts.first())
    }
}