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
import java.nio.file.attribute.FileTime
import java.time.Instant
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories

class ManualSearchStrategyTest {
    private lateinit var fixture: Fixture

    @BeforeEach
    fun setUp() {
        fixture = Fixture()
    }

    @ParameterizedTest(name = "should return static path for architecture {0}")
    @MethodSource("architectureMappingProvider")
    fun `should return static path when framework exists`(
        expectedArchitecture: String, @TempDir dir: Path
    ) {
        val xcframeworkPath = dir.resolve("somewhere/hidden/Sentry.xcframework").createDirectories()
        val archDirectory = Files.createDirectory(xcframeworkPath.resolve(expectedArchitecture))

        val sut = fixture.getSut(dir.absolutePathString())
        val paths = sut.resolvePaths(expectedArchitecture)

        assertEquals(archDirectory.absolutePathString(), paths.static)
        assertNull(paths.dynamic)
    }

    @ParameterizedTest(name = "should return dynamic path for architecture {0}")
    @MethodSource("architectureMappingProvider")
    fun `should return dynamic path when framework exists`(
        expectedArchitecture: String, @TempDir dir: Path
    ) {
        val xcframeworkPath = dir.resolve("somewhere/hidden/Sentry-Dynamic.xcframework").createDirectories()
        val archDirectory = Files.createDirectory(xcframeworkPath.resolve(expectedArchitecture))

        val sut = fixture.getSut(dir.absolutePathString())
        val paths = sut.resolvePaths(expectedArchitecture)

        assertEquals(archDirectory.absolutePathString(), paths.dynamic)
        assertNull(paths.static)
    }

    @ParameterizedTest(name = "should return most recently used path for architecture {0}")
    @MethodSource("architectureMappingProvider")
    fun `should return most recently used path when multiple framework exists`(
        expectedArchitecture: String, @TempDir dir: Path
    ) {
        val temp = dir.resolve("somewhere/hidden/Sentry.xcframework").createDirectories()
        // Modifying this path so it's modified longer ago than the second path
        val xcframeworkPath1 = Files.setLastModifiedTime(temp, FileTime.from(Instant.now().minusSeconds(5)))
        Files.createDirectory(xcframeworkPath1.resolve(expectedArchitecture))

        val xcframeworkPath2 = dir.resolve("more/recent/Sentry.xcframework").createDirectories()
        val archDirectory2 = Files.createDirectory(xcframeworkPath2.resolve(expectedArchitecture))

        val sut = fixture.getSut(dir.absolutePathString())
        val paths = sut.resolvePaths(expectedArchitecture)

        assertEquals(archDirectory2.absolutePathString(), paths.static)
        assertNull(paths.dynamic)
    }

    @Test
    fun `returns NONE when no framework found`() {
        val sut = fixture.getSut("some/random/path")
        val result = sut.resolvePaths("doesnt matter")

        assertEquals(FrameworkPaths.NONE, result)
    }

    companion object {
        @JvmStatic
        fun architectureMappingProvider() = SentryCocoaFrameworkArchitectures.all.flatten()
            .map { Arguments.of(it) }
            .toList()
    }

    private class Fixture {
        fun getSut(basePathToSearch: String): ManualSearchStrategy {
            val project = ProjectBuilder.builder().build()

            project.pluginManager.apply {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("io.sentry.kotlin.multiplatform.gradle")
            }

            return ManualSearchStrategy(project, basePathToSearch)
        }
    }
}