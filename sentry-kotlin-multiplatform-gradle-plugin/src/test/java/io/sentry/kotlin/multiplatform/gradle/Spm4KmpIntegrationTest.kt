package io.sentry.kotlin.multiplatform.gradle

import io.github.frankois944.spmForKmp.definition.PackageRootDefinitionExtension
import io.github.frankois944.spmForKmp.swiftPackageConfig
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.sentry.BuildConfig
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.logging.Logger
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.konan.target.HostManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class Spm4KmpIntegrationTest {
    @Test
    fun `generated container uses Cocoa 9 minimums`() {
        val project = createProject()
        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kotlin.apply {
            iosArm64()
            iosX64()
            iosSimulatorArm64()
            tvosArm64()
            tvosX64()
            tvosSimulatorArm64()
            macosArm64()
            macosX64()
            watchosArm32("legacyWatch")
            watchosArm64()
            watchosX64()
            watchosSimulatorArm64()
        }

        (project as ProjectInternal).evaluate()

        assertEquals(11, packages(project).size)
        packages(project).forEach {
            assertEquals("15.0", it.minIos)
            assertEquals("15.0", it.minTvos)
            assertEquals("12.0", it.minMacos)
            assertEquals("9.0", it.minWatchos)
        }
        val manifest = generateContainer(project)
        assertPlatforms(manifest, "15.0", "15.0", "12.0", "9.0")
        assertSentryDependency(manifest, BuildConfig.SentryCocoaVersion)
    }

    @ParameterizedTest
    @CsvSource("16.2,17.0,13.1,10.0", ",14.9,10.15,8.9")
    fun `consumer defaults are raised only when below Cocoa minimums`(
        ios: String?,
        tvos: String,
        macos: String,
        watchos: String,
    ) {
        val project = createProject()
        packages(project).configureEach {
            it.minIos = ios
            it.minTvos = tvos
            it.minMacos = macos
            it.minWatchos = watchos
        }
        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kotlin.iosArm64()
        kotlin.watchosArm64()
        (project as ProjectInternal).evaluate()

        if (ios == null) {
            assertPlatforms(generateContainer(project), "15.0", "15.0", "12.0", "9.0")
        } else {
            assertPlatforms(generateContainer(project), ios, tvos, macos, watchos)
        }
    }

    @Test
    fun `stub target skips Cocoa installation`() {
        val project = createProject()
        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        // Check the Konan target, even if the consumer assigns a custom name.
        val legacyWatch = kotlin.watchosArm32("legacyWatch")
        (project as ProjectInternal).evaluate()

        assertTrue(packages(project).isEmpty())
        assertTrue(
            legacyWatch.compilations
                .getByName("main")
                .cinterops
                .isEmpty(),
        )
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `existing user minimums remain unchanged for target and global configs`(global: Boolean) {
        val project = createProject()
        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val target = kotlin.iosArm64()
        if (global) {
            packages(project).create(SENTRY_COCOA_CINTEROP_NAME)
        } else {
            target.swiftPackageConfig(cinteropName = SENTRY_COCOA_CINTEROP_NAME) { }
        }
        val entry = packages(project).single()
        // User-owned packages can deliberately target historical Cocoa releases.
        entry.minIos = "14.0"
        entry.minTvos = "17.0"
        entry.minMacos = "13.0"
        entry.minWatchos = "8.0"
        kotlin.watchosArm32()

        (project as ProjectInternal).evaluate()

        assertEquals(1, packages(project).size)
        assertEquals("14.0", entry.minIos)
        assertEquals("17.0", entry.minTvos)
        assertEquals("13.0", entry.minMacos)
        assertEquals("8.0", entry.minWatchos)
    }

    @ParameterizedTest
    @CsvSource("true,true,false,false", "false,true,false,false", "true,false,false,false", "true,true,true,false", "true,true,false,true")
    fun `version warning only applies to overridden auto installed packages`(
        overrideVersion: Boolean,
        enabled: Boolean,
        userOwned: Boolean,
        stubOnly: Boolean,
    ) {
        val project = spyk(createProject())
        val logger = mockk<Logger>(relaxed = true)
        every { project.logger } returns logger
        val autoInstall = project.extensions.getByName("autoInstall") as AutoInstallExtension
        val version = if (overrideVersion) "9.27.0" else BuildConfig.SentryCocoaVersion
        autoInstall.spm.sentryCocoaVersion.set(version)
        autoInstall.spm.enabled.set(enabled)
        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        if (stubOnly) {
            kotlin.watchosArm32()
        } else {
            kotlin.iosArm64()
            kotlin.iosSimulatorArm64()
        }
        if (userOwned) packages(project).create(SENTRY_COCOA_CINTEROP_NAME)

        project.installSentryForSpm4Kmp(autoInstall)

        verify(exactly = if (overrideVersion && enabled && !userOwned && !stubOnly) 1 else 0) {
            logger.warn(match<String> { it.contains("autoInstall.spm.sentryCocoaVersion") })
        }
        if (enabled && !userOwned && !stubOnly) {
            (project as ProjectInternal).evaluate()
            assertSentryDependency(generateContainer(project), version)
        }
    }

    private fun createProject(): Project {
        assumeTrue(HostManager.hostIsMac)
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")
        return project
    }

    @Suppress("UNCHECKED_CAST")
    private fun packages(project: Project): NamedDomainObjectContainer<PackageRootDefinitionExtension> =
        project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<PackageRootDefinitionExtension>

    private fun generateContainer(project: Project): String {
        // Execute only spm4Kmp's real manifest writer: no Swift resolution or native compilation.
        val task = project.tasks.getByName("SwiftPackageConfigAppleSentryCocoaGenerateSwiftPackage")
        task.actions.forEach { it.execute(task) }
        return task.outputs.files.singleFile
            .readText()
    }

    private fun assertSentryDependency(
        manifest: String,
        version: String,
    ) {
        val dependency = """.package(url: "https://github.com/getsentry/sentry-cocoa.git", exact: "$version")"""
        assertTrue(manifest.contains(dependency), manifest)
    }

    private fun assertPlatforms(
        manifest: String,
        ios: String,
        tvos: String,
        macos: String,
        watchos: String,
    ) {
        assertTrue(manifest.contains(".iOS(\"$ios\")"), manifest)
        assertTrue(manifest.contains(".tvOS(\"$tvos\")"), manifest)
        assertTrue(manifest.contains(".macOS(\"$macos\")"), manifest)
        assertTrue(manifest.contains(".watchOS(\"$watchos\")"), manifest)
    }
}
