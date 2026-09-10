package io.sentry.kotlin.multiplatform.gradle

import io.github.frankois944.spmForKmp.definition.PackageRootDefinitionExtension
import io.github.frankois944.spmForKmp.swiftPackageConfig
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.konan.target.HostManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class Spm4KmpIntegrationTest {
    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `generated container uses Cocoa 9 minimums in either plugin order`(spmFirst: Boolean) {
        val project = createProject(spmFirst)
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
        assertTrue(manifest.contains("9.28.0"), manifest)
        assertTrue(manifest.contains("https://github.com/getsentry/sentry-cocoa.git"), manifest)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `higher consumer defaults survive auto installation and container generation`(spmFirst: Boolean) {
        val project = createProject(spmFirst)
        packages(project).configureEach {
            it.minIos = "16.2"
            it.minTvos = "17.0"
            it.minMacos = "13.1"
            // Numeric comparison must keep 10 above 9.
            it.minWatchos = "10.0"
        }
        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kotlin.iosArm64()
        kotlin.watchosArm64()

        (project as ProjectInternal).evaluate()

        assertPlatforms(generateContainer(project), "16.2", "17.0", "13.1", "10.0")
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `unsupported target fails with an actionable diagnostic in either plugin order`(spmFirst: Boolean) {
        val project = createProject(spmFirst)
        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

        val failure =
            assertThrows<Exception> {
                // Check the Konan target, even if the consumer assigns a custom name.
                kotlin.watchosArm32("legacyWatch")
                (project as ProjectInternal).evaluate()
            }
        val messages = generateSequence<Throwable>(failure) { it.cause }.joinToString { it.message.orEmpty() }
        assertTrue(messages.contains("watchosArm32 (legacyWatch)"), messages)
        assertTrue(messages.contains("armv7k"), messages)
        assertTrue(messages.contains("watchosArm64()"), messages)
        assertTrue(messages.contains("autoInstall.spm.enabled = false"), messages)
        assertTrue(packages(project).isEmpty())
        assertFalse(project.extensions.extraProperties.has(SPM_AUTO_INSTALLED_MARKER))
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `opt outs do not reject unsupported target`(disableGlobally: Boolean) {
        val project = createProject(spmFirst = true)
        val autoInstall = project.extensions.getByName("autoInstall") as AutoInstallExtension
        if (disableGlobally) {
            autoInstall.enabled.set(false)
        } else {
            autoInstall.spm.enabled.set(false)
        }
        project.extensions.getByType(KotlinMultiplatformExtension::class.java).watchosArm32()

        (project as ProjectInternal).evaluate()

        assertTrue(packages(project).isEmpty())
        assertFalse(project.extensions.extraProperties.has(SPM_AUTO_INSTALLED_MARKER))
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `existing user minimums remain unchanged for target and global configs`(global: Boolean) {
        val project = createProject(spmFirst = false)
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
        assertFalse(project.extensions.extraProperties.has(SPM_AUTO_INSTALLED_MARKER))
    }

    @Test
    fun `null and lower defaults are raised on every platform`() {
        val project = createProject(spmFirst = true)
        packages(project).configureEach {
            it.minIos = null
            it.minTvos = "14.9"
            it.minMacos = "10.15"
            it.minWatchos = "8.9"
        }
        project.extensions.getByType(KotlinMultiplatformExtension::class.java).iosArm64()

        (project as ProjectInternal).evaluate()

        assertPlatforms(generateContainer(project), "15.0", "15.0", "12.0", "9.0")
    }

    private fun createProject(spmFirst: Boolean): Project {
        assumeTrue(HostManager.hostIsMac)
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        val plugins = listOf("io.github.frankois944.spmForKmp", "io.sentry.kotlin.multiplatform.gradle")
        (if (spmFirst) plugins else plugins.reversed()).forEach { project.pluginManager.apply(it) }
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
