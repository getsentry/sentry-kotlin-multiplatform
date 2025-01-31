package io.sentry.kotlin.multiplatform.gradle

import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SentryFrameworkPathResolutionTest {
    @TempDir
    lateinit var tempDir: File

    @Test
    fun `resolves dynamic framework path when it exists`() {
        // Setup mock derived data structure
        val derivedDataPath = setupMockDerivedDataStructure(isDynamic = true)
        
        // Create test project and target
        val project = ProjectBuilder.builder().build()
        val target = mockk<KotlinNativeTarget>()
        every { target.name } returns "iosArm64"
        every { target.konanTarget.family.isAppleFamily } returns true
        
        // Create framework directories
        val frameworkPath = "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry-Dynamic/Sentry-Dynamic.xcframework/ios-arm64"
        File(frameworkPath).mkdirs()
        
        // Test framework resolution
        val result = project.findFrameworkPath(
            target = target,
            derivedDataPath = derivedDataPath,
            frameworkPath = null
        )
        
        assertNotNull(result)
        assertTrue(result.first.contains("ios-arm64"))
        assertTrue(result.second.contains("Sentry-Dynamic.xcframework"))
    }

    @Test
    fun `resolves static framework path when dynamic doesn't exist`() {
        // Setup mock derived data structure
        val derivedDataPath = setupMockDerivedDataStructure(isDynamic = false)
        
        // Create test project and target
        val project = ProjectBuilder.builder().build()
        val target = mockk<KotlinNativeTarget>()
        every { target.name } returns "iosArm64"
        every { target.konanTarget.family.isAppleFamily } returns true
        
        // Create framework directories
        val frameworkPath = "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry/Sentry.xcframework/ios-arm64"
        File(frameworkPath).mkdirs()
        
        // Test framework resolution
        val result = project.findFrameworkPath(
            target = target,
            derivedDataPath = derivedDataPath,
            frameworkPath = null
        )
        
        assertNotNull(result)
        assertTrue(result.first.contains("ios-arm64"))
        assertTrue(result.second.contains("Sentry.xcframework"))
    }

    private fun setupMockDerivedDataStructure(isDynamic: Boolean): String {
        val derivedDataPath = tempDir.resolve("DerivedData").absolutePath
        val baseDir = if (isDynamic) {
            File("$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry-Dynamic")
        } else {
            File("$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry")
        }
        baseDir.mkdirs()
        return derivedDataPath
    }
} 