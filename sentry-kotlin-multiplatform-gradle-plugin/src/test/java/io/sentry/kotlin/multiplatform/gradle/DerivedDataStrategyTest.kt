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
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class DerivedDataStrategyTest {
    private lateinit var fixture: Fixture

    @BeforeEach
    fun setUp() {
        fixture = Fixture()
    }

    @ParameterizedTest(name = "resolve static path for architecture {0}")
    @MethodSource("architectureMappingProvider")
    fun `if xcodeproj is null and find xcode project successfully then resolve static path`(
        expectedArchitecture: Set<String>,
        @TempDir dir: Path
    ) {
        Files.createDirectory(dir.resolve("project.xcodeproj"))
        val xcframeworkPath =
            dir.resolve("SourcePackages/artifacts/sentry-cocoa/Sentry/Sentry.xcframework")
        val xcframeworkDirectory = Files.createDirectories(xcframeworkPath)
        val archDirectory =
            Files.createDirectory(xcframeworkDirectory.resolve(expectedArchitecture.first()))

        val sut = fixture.getSut(null, rootDirPath = dir.toFile().absolutePath) { _: String ->
            dir.toFile().absolutePath
        }

        val paths = sut.resolvePaths(expectedArchitecture)
        assertEquals(archDirectory.absolutePathString(), paths.static)
        assertNull(paths.dynamic)
    }

    @ParameterizedTest(name = "should return dynamic path for architecture {0}")
    @MethodSource("architectureMappingProvider")
    fun `if xcodeproj is null and find xcode project successfully then resolve dynamic path`(
        expectedArchitecture: Set<String>,
        @TempDir dir: Path
    ) {
        Files.createDirectory(dir.resolve("project.xcodeproj"))
        val xcframeworkPath =
            dir.resolve("SourcePackages/artifacts/sentry-cocoa/Sentry-Dynamic/Sentry-Dynamic.xcframework")
        val xcframeworkDirectory = Files.createDirectories(xcframeworkPath)
        val archDirectory =
            Files.createDirectory(xcframeworkDirectory.resolve(expectedArchitecture.first()))

        val sut = fixture.getSut(null, rootDirPath = dir.toFile().absolutePath) { _: String ->
            dir.toFile().absolutePath
        }

        val paths = sut.resolvePaths(expectedArchitecture)
        assertEquals(archDirectory.absolutePathString(), paths.dynamic)
        assertNull(paths.static)
    }

    @Test
    fun `if xcodeproj is null and find xcode project is not successful then return NONE`(
        @TempDir dir: Path
    ) {
        val sut = fixture.getSut(null, rootDirPath = dir.toFile().absolutePath) { _: String ->
            dir.toFile().absolutePath
        }

        val paths = sut.resolvePaths(setOf("doesnt matter"))
        assertEquals(FrameworkPaths.NONE, paths)
    }

    @Test
    fun `if xcodeproj is not null and find xcode project is not successful then return NONE`(
        @TempDir dir: Path
    ) {
        val sut = fixture.getSut(
            "some invalid path",
            rootDirPath = dir.toFile().absolutePath
        ) { _: String ->
            dir.toFile().absolutePath
        }

        val paths = sut.resolvePaths(setOf("doesnt matter"))
        assertEquals(FrameworkPaths.NONE, paths)
    }

    companion object {
        @JvmStatic
        fun architectureMappingProvider() = SentryCocoaFrameworkArchitectures.all
            .map { Arguments.of(it) }
            .toList()
    }

    private class Fixture {
        fun getSut(
            xcodeprojPath: String?,
            rootDirPath: String,
            derivedDataProvider: (String) -> String?
        ): DerivedDataStrategy {
            val project = ProjectBuilder.builder()
                .withProjectDir(File(rootDirPath))
                .build()

            project.pluginManager.apply {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("io.sentry.kotlin.multiplatform.gradle")
            }

            val linker = project.extensions.getByType(LinkerExtension::class.java)
            linker.xcodeprojPath.set(xcodeprojPath)

            return DerivedDataStrategy(project, derivedDataProvider)
        }
    }
}
