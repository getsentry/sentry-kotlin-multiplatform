package io.sentry.kotlin.multiplatform.gradle

import io.github.frankois944.spmForKmp.definition.PackageRootDefinitionExtension
import io.github.frankois944.spmForKmp.swiftPackageConfig
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.Copy
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.konan.target.HostManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class Spm4KmpWatchosSimulatorTest {
    @ParameterizedTest
    @CsvSource("true, true", "true, false", "false, true", "false, false")
    fun `copy uses shared configuration and precedes cinterop in either plugin order`(
        spmFirst: Boolean,
        debug: Boolean,
    ) {
        val project = createProject(spmFirst)
        val entries = project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<*>
        entries.configureEach { entry ->
            entry as PackageRootDefinitionExtension
            // Exercise relative and absolute roots, and spm4Kmp's first-entry selection.
            entry.spmWorkingPath =
                if (entry.name.endsWith("_IosArm64")) {
                    if (debug) "custom-spm" else project.file("custom-spm").absolutePath
                } else {
                    project.file("unused-watchos-spm").absolutePath
                }
            entry.debug = if (entry.name.endsWith("_IosArm64")) debug else !debug
        }
        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kotlin.iosArm64()
        kotlin.watchosSimulatorArm64()
        (project as ProjectInternal).evaluate()

        val copy = project.tasks.getByName(COPY_TASK_NAME) as Copy
        val compile = project.tasks.getByName("${SPM_TASK_PREFIX}CompileSwiftPackageWatchosSimulatorArm64")
        val definition = project.tasks.getByName("${SPM_TASK_PREFIX}GenerateCInteropDefinitionWatchosSimulatorArm64")
        val cinterop = project.tasks.getByName("cinteropSentryCocoaWatchosSimulatorArm64")
        assertTrue(compile in copy.taskDependencies.getDependencies(copy))
        assertTrue(copy in definition.taskDependencies.getDependencies(definition))
        assertTrue(definition in cinterop.taskDependencies.getDependencies(cinterop))
        assertEquals(1, project.tasks.names.count { it == COPY_TASK_NAME })

        verifyCopy(project, copy, debug)
    }

    private fun verifyCopy(
        project: Project,
        copy: Copy,
        debug: Boolean,
    ) {
        val scratch = project.file("custom-spm/spmKmpPlugin/sentryCocoa/scratch")
        val artifact = scratch.resolve("artifacts/sentry-cocoa/Sentry/Sentry.xcframework")
        val framework = artifact.resolve("watchos-arm64_x86_64-simulator/Sentry.framework")
        framework.resolve("Headers").mkdirs()
        framework.resolve("Sentry").writeText("simulator binary")
        framework.resolve("Headers/Sentry.h").writeText("simulator header")
        val device = artifact.resolve("watchos-arm64_arm64_32_arm64e/Sentry.framework")
        device.mkdirs()
        device.resolve("Sentry").writeText("device binary must not be copied")
        val destination = scratch.resolve("aarch64-apple-watchos-simulator/${if (debug) "debug" else "release"}")
        destination.mkdirs()
        destination.resolve("libsentryCocoa.a").writeText("existing bridge")

        // Run only the real Copy action against a local fixture, without SwiftPM or compilation.
        copy.actions.forEach { it.execute(copy) }

        assertEquals(destination, copy.destinationDir)
        assertEquals("simulator binary", destination.resolve("Sentry.framework/Sentry").readText())
        assertEquals("simulator header", destination.resolve("Sentry.framework/Headers/Sentry.h").readText())
        assertEquals("existing bridge", destination.resolve("libsentryCocoa.a").readText())
        assertFalse(destination.resolve("watchos-arm64_x86_64-simulator").exists())
        assertFalse(project.file("unused-watchos-spm/spmKmpPlugin/sentryCocoa/scratch").exists())
    }

    @ParameterizedTest
    @CsvSource("true, true", "true, false", "false, true", "false, false")
    fun `opt outs do not register copy`(
        spmFirst: Boolean,
        disableGlobally: Boolean,
    ) {
        val project = createProject(spmFirst)
        val autoInstall = project.extensions.getByName("autoInstall") as AutoInstallExtension
        if (disableGlobally) autoInstall.enabled.set(false) else autoInstall.spm.enabled.set(false)
        project.extensions.getByType(KotlinMultiplatformExtension::class.java).watchosSimulatorArm64()

        (project as ProjectInternal).evaluate()

        assertNull(project.tasks.findByName(COPY_TASK_NAME))
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `user owned packages do not register copy`(global: Boolean) {
        val project = createProject(spmFirst = false)
        val target = project.extensions.getByType(KotlinMultiplatformExtension::class.java).watchosSimulatorArm64()
        if (global) {
            val entries = project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<*>
            entries.create(SENTRY_COCOA_CINTEROP_NAME)
        } else {
            target.swiftPackageConfig(cinteropName = SENTRY_COCOA_CINTEROP_NAME) { }
        }

        (project as ProjectInternal).evaluate()

        assertNull(project.tasks.findByName(COPY_TASK_NAME))
    }

    @Test
    fun `other targets do not register copy`() {
        val project = createProject(spmFirst = true)
        project.extensions.getByType(KotlinMultiplatformExtension::class.java).apply {
            iosSimulatorArm64()
            watchosArm64()
            watchosX64()
        }

        (project as ProjectInternal).evaluate()

        assertNull(project.tasks.findByName(COPY_TASK_NAME))
    }

    private fun createProject(spmFirst: Boolean): Project {
        assumeTrue(HostManager.hostIsMac)
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        val plugins = listOf("io.github.frankois944.spmForKmp", "io.sentry.kotlin.multiplatform.gradle")
        (if (spmFirst) plugins else plugins.reversed()).forEach { project.pluginManager.apply(it) }
        return project
    }

    private companion object {
        const val COPY_TASK_NAME = "copySentrySpmWatchosSimulatorFramework"
        const val SPM_TASK_PREFIX = "SwiftPackageConfigAppleSentryCocoa"
    }
}
