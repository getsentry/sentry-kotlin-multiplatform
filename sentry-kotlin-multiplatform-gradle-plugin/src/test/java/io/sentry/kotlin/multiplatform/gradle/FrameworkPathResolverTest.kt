package io.sentry.kotlin.multiplatform.gradle

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.gradle.api.Project
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import java.io.File

@ExtendWith(MockKExtension::class)
class FrameworkPathResolverTest {
    @TempDir
    lateinit var tempDir: File

    @MockK
    lateinit var mockLinkerExtension: LinkerExtension

    lateinit var mockProject: Project

    lateinit var resolver: FrameworkPathResolver

    @BeforeEach
    fun setup() {
        mockProject = mockk<Project> {
            every { providers } returns mockk {
                every { of(any<Class<DerivedDataPathValueSource>>(), any()) } returns mockk {
                    every { get() } returns tempDir.resolve("DerivedData").absolutePath
                }
            }
        }
        resolver = FrameworkPathResolver(mockProject)
    }

    @Test
    fun `resolves valid static custom path`() {
        val frameworkDir = File(tempDir, "Sentry.xcframework").apply { mkdir() }

        val (dynamic, static) = resolver.resolvePaths(
            mockLinkerExtension,
            architectures = setOf("ios-arm64")
        )

        assertEquals(null to frameworkDir.absolutePath, dynamic to static)
    }
}