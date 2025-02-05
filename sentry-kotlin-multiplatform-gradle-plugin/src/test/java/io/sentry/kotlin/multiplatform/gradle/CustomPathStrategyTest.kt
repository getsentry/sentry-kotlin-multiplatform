package io.sentry.kotlin.multiplatform.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class CustomPathStrategyTest {
    private lateinit var fixture: Fixture

    @BeforeEach
    fun setUp() {
        fixture = Fixture()
    }

    @ParameterizedTest(name = "should return static path for architecture {0}")
    @MethodSource("architectureMappingProvider")
    fun `should return static path when framework is Sentry xcframework`(
        expectedArchitecture: String,
        @TempDir dir: Path
    ) {
        val xcframeworkPath = dir.resolve("Sentry.xcframework")
        Files.createDirectory(xcframeworkPath)
        val archDirectory = Files.createDirectory(xcframeworkPath.resolve(expectedArchitecture))

        val sut = fixture.getSut(xcframeworkPath.absolutePathString())
        val paths = sut.resolvePaths(expectedArchitecture)

        assertEquals(archDirectory.absolutePathString(), paths.static)
        assertNull(paths.dynamic)
    }

    @ParameterizedTest(name = "should return dynamic path for architecture {0}")
    @MethodSource("architectureMappingProvider")
    fun `should return dynamic path when framework is Sentry xcframework`(
        expectedArchitecture: String,
        @TempDir dir: Path
    ) {
        val xcframeworkPath = dir.resolve("Sentry-Dynamic.xcframework")
        Files.createDirectory(xcframeworkPath)
        val archDirectory = Files.createDirectory(xcframeworkPath.resolve(expectedArchitecture))

        val sut = fixture.getSut(xcframeworkPath.absolutePathString())
        val paths = sut.resolvePaths(expectedArchitecture)

        assertEquals(archDirectory.absolutePathString(), paths.dynamic)
        assertNull(paths.static)
    }

    @Test
    fun `returns NONE when frameworkPath is null`() {
        val sut = fixture.getSut(null)
        val result = sut.resolvePaths("doesnt matter")

        assertEquals(FrameworkPaths.NONE, result)
    }

    @Test
    fun `returns NONE when frameworkPath is empty`() {
        val sut = fixture.getSut("")
        val result = sut.resolvePaths("doesnt matter")

        assertEquals(FrameworkPaths.NONE, result)
    }

    @Test
    fun `should return NONE when framework has invalid name`(@TempDir dir: Path) {
        val xcframeworkPath = dir.resolve("Invalid.xcframework")
        val sut = fixture.getSut(xcframeworkPath.absolutePathString())

        val paths = sut.resolvePaths("doesnt matter")

        assertEquals(FrameworkPaths.NONE, paths)
    }

    companion object {
        @JvmStatic
        fun architectureMappingProvider() = SentryCocoaFrameworkArchitectures.all.flatten()
            .map { Arguments.of(it) }
            .toList()
    }

    private class Fixture {
        fun getSut(frameworkPath: String?): CustomPathStrategy {
            val project = ProjectBuilder.builder().build()

            project.pluginManager.apply {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("io.sentry.kotlin.multiplatform.gradle")
            }

            val linker = project.extensions.getByType(LinkerExtension::class.java)
            linker.frameworkPath.set(frameworkPath)

            return CustomPathStrategy(project)
        }
    }
}
