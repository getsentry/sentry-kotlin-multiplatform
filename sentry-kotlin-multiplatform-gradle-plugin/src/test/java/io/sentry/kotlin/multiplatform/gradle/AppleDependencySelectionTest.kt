@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

package io.sentry.kotlin.multiplatform.gradle

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkObject
import io.mockk.verify
import io.sentry.BuildConfig
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionAware
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.swiftimport.SwiftPMDependency
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.swiftimport.SwiftPMImportExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

@EnabledOnOs(OS.MAC)
class AppleDependencySelectionTest {
    @ParameterizedTest
    @CsvSource(
        "false,false,SWIFT_PM",
        "true,false,SPM4KMP",
        "false,true,NONE",
        "true,true,SPM4KMP",
    )
    fun `AUTO prefers spm4Kmp, skips CocoaPods projects, and otherwise uses SwiftPM`(
        spm: Boolean,
        pods: Boolean,
        expected: AppleDependencyProvider,
    ) {
        val project = project()
        if (spm) project.pluginManager.apply(SPM4KMP_PLUGIN_ID)
        if (pods) project.pluginManager.apply(COCOAPODS_PLUGIN_ID)
        assertEquals(expected, project.resolveAppleDependencyProvider(options(project)))
    }

    @ParameterizedTest
    @EnumSource(AppleDependencyProvider::class)
    fun `global disable bypasses provider validation`(provider: AppleDependencyProvider) {
        val project = project()
        options(project).enabled.set(false)
        options(project).apple.provider.set(provider)
        assertEquals(AppleDependencyProvider.NONE, project.resolveAppleDependencyProvider(options(project)))
        configure(project)
        assertTrue(swift(project).swiftPMDependencies.isEmpty())
        assertTrue(
            project.configurations
                .getByName("commonMainApi")
                .dependencies
                .isEmpty(),
        )
    }

    @Test
    fun `unavailable explicit provider fails without fallback`() {
        val project = project()
        options(project).apple.provider.set(AppleDependencyProvider.SPM4KMP)
        val error = assertThrows(GradleException::class.java) { project.resolveAppleDependencyProvider(options(project)) }
        assertTrue(error.message!!.contains("SPM4KMP is unavailable"))
    }

    @Test
    fun `no install reason names the cause`() {
        val project = project()
        assertTrue(project.noAppleDependencyReason(AppleDependencyProvider.NONE).contains("provider is NONE"))
        project.pluginManager.apply(COCOAPODS_PLUGIN_ID)
        assertTrue(project.noAppleDependencyReason(AppleDependencyProvider.AUTO).contains("CocoaPods plugin is applied"))
    }

    @ParameterizedTest
    @CsvSource("AUTO,true", "NONE,false")
    fun `no install reason is visible only when AUTO decided`(
        provider: AppleDependencyProvider,
        visible: Boolean,
    ) {
        val project = spyk(project())
        val logger = mockk<Logger>(relaxed = true)
        every { project.logger } returns logger
        project.pluginManager.apply(COCOAPODS_PLUGIN_ID)
        options(project).apple.provider.set(provider)
        configure(project)
        val isReason: (String) -> Boolean = { it.startsWith("Sentry Cocoa is not installed automatically") }
        verify(exactly = if (visible) 1 else 0) { logger.lifecycle(match<String>(isReason)) }
        verify(exactly = if (visible) 0 else 1) { logger.info(match<String>(isReason)) }
    }

    @Test
    fun `explicit SwiftPM fails with the CocoaPods plugin`() {
        val project = project()
        project.pluginManager.apply(COCOAPODS_PLUGIN_ID)
        options(project).apple.provider.set(AppleDependencyProvider.SWIFT_PM)
        val error = assertThrows(GradleException::class.java) { project.resolveAppleDependencyProvider(options(project)) }
        assertTrue(error.message!!.contains("cannot be used with the Kotlin CocoaPods plugin"))
    }

    @Test
    fun `NONE keeps commonMain and registers nothing`() {
        val project = project()
        project.pluginManager.apply(SPM4KMP_PLUGIN_ID)
        project.pluginManager.apply(COCOAPODS_PLUGIN_ID)
        kotlin(project).iosArm64()
        options(project).apple.provider.set(AppleDependencyProvider.NONE)
        configure(project)
        assertTrue(swift(project).swiftPMDependencies.isEmpty())
        assertFalse(project.isSentryConfiguredViaSpm4Kmp("iosArm64"))
        val pods = (kotlin(project) as ExtensionAware).extensions.getByType(CocoapodsExtension::class.java)
        assertNull(pods.pods.findByName("Sentry"))
        assertTrue(
            project.configurations
                .getByName("commonMainApi")
                .dependencies
                .any { it.name == "sentry-kotlin-multiplatform" },
        )
    }

    @Test
    fun `explicit spm4Kmp does not register through SwiftPM`() {
        val project = project()
        project.pluginManager.apply(SPM4KMP_PLUGIN_ID)
        kotlin(project).iosArm64()
        options(project).apple.provider.set(AppleDependencyProvider.SPM4KMP)
        configure(project)
        assertTrue(project.isSentryConfiguredViaSpm4Kmp("iosArm64"))
        assertTrue(swift(project).swiftPMDependencies.isEmpty())
    }

    @Test
    fun `AUTO SwiftPM pins Cocoa and preserves higher platform minimums`() {
        val project = project()
        kotlin(project).apply {
            iosArm64()
            macosArm64()
            tvosArm64()
            watchosArm64()
        }
        swift(project).iosMinimumDeploymentTarget.set("16.1")
        swift(project).macosMinimumDeploymentTarget.set("11.0")
        configure(project)
        val dependency = swift(project).swiftPMDependencies.single() as SwiftPMDependency.Remote
        assertEquals(BuildConfig.SentryCocoaVersion, dependency.exactVersion())
        assertEquals("Sentry", dependency.products.single().name)
        assertEquals(
            4,
            dependency.products
                .single()
                .platformConstraints!!
                .size,
        )
        assertEquals("16.1", swift(project).iosMinimumDeploymentTarget.get())
        assertEquals("12.0", swift(project).macosMinimumDeploymentTarget.get())
        assertEquals("15.0", swift(project).tvosMinimumDeploymentTarget.get())
        assertEquals("9.0", swift(project).watchosMinimumDeploymentTarget.get())
    }

    @Test
    fun `Cocoa version override applies to SwiftPM`() {
        val project = project()
        kotlin(project).iosArm64()
        options(project).apple.sentryCocoaVersion.set("9.27.0")
        configure(project)
        val dependency = swift(project).swiftPMDependencies.single() as SwiftPMDependency.Remote
        assertEquals("9.27.0", dependency.exactVersion())
    }

    @Test
    fun `stub-only target does not install Cocoa`() {
        val project = project()
        kotlin(project).watchosArm32()
        options(project).apple.provider.set(AppleDependencyProvider.SWIFT_PM)
        configure(project)
        assertTrue(swift(project).swiftPMDependencies.isEmpty())
    }

    @Test
    fun `explicit SwiftPM does not require spm ordering when spm is applied first`() {
        val project = spmFirstProject()
        options(project).apple.provider.set(AppleDependencyProvider.SWIFT_PM)
        SentryPlugin().executeConfiguration(project, hostIsMac = true, spmAppliedFirst = true)
        assertEquals(1, swift(project).swiftPMDependencies.size)
        assertFalse(project.isSentryConfiguredViaSpm4Kmp("iosArm64"))
    }

    @Test
    fun `AUTO with spm applied first still requires plugin ordering`() {
        val project = spmFirstProject()
        val error =
            assertThrows(GradleException::class.java) {
                SentryPlugin().executeConfiguration(project, hostIsMac = true, spmAppliedFirst = true)
            }
        assertTrue(error.message!!.contains("Move the Sentry plugin before spm4Kmp"))
    }

    @Test
    fun `after evaluation registration keeps other user declarations`() {
        val project = project()
        kotlin(project).iosArm64()
        declarePackage(project, "Other", "https://example.com/other.git")
        (project as ProjectInternal).evaluate()
        assertEquals(2, swift(project).swiftPMDependencies.size)
    }

    @Test
    fun `user Sentry declarations are not inspected`() {
        val project = project()
        kotlin(project).iosArm64()
        declarePackage(project, "Sentry", SENTRY_COCOA_GIT_URL)
        configure(project)
        assertEquals(2, swift(project).swiftPMDependencies.size)
    }

    @ParameterizedTest
    @ValueSource(classes = [NoSuchMethodError::class, NoClassDefFoundError::class, ClassCastException::class])
    fun `incompatible SwiftPM API fails with recovery steps`(errorType: Class<out Throwable>) {
        val project = project()
        kotlin(project).iosArm64()
        val cause = errorType.getConstructor(String::class.java).newInstance("swiftPackage")
        mockkObject(OfficialSwiftPmIntegration)
        try {
            every { OfficialSwiftPmIntegration.install(any(), any(), any()) } throws cause
            val error = assertThrows(GradleException::class.java) { configure(project) }
            assertSame(cause, error.cause)
            assertTrue(error.message!!.contains("could not install Sentry Cocoa with Kotlin"))
        } finally {
            unmockkObject(OfficialSwiftPmIntegration)
        }
    }

    @Test
    fun `SwiftPM incompatibility message gives copyable recovery steps`() {
        val project = project()
        project.pluginManager.apply(SPM4KMP_PLUGIN_ID)
        kotlin(project).apply {
            iosArm64()
            macosArm64()
            watchosArm32()
        }

        val message = project.swiftPmIncompatibilityMessage("9.27.0")

        assertTrue(message.contains("Sentry KMP Gradle plugin ${BuildConfig.SentryKmpVersion}"))
        assertTrue(message.contains("same Kotlin Gradle plugin version in all modules"))
        assertTrue(message.contains("use Kotlin ${BuildConfig.KotlinGradlePluginVersion} until"))
        assertTrue(message.contains("autoInstall.apple.provider.set(AppleDependencyProvider.NONE)"))
        assertTrue(message.contains("linker.enabled.set(false)"))
        assertTrue(message.contains("url = url(\"$SENTRY_COCOA_GIT_URL\")"))
        assertTrue(message.contains("version = exact(\"9.27.0\")"))
        assertTrue(message.contains("products = listOf(product(\"Sentry\", importedClangModules = emptySet()))"))
        assertTrue(message.contains("                iosMinimumDeploymentTarget.set(\"$SENTRY_COCOA_MIN_IOS\")"))
        assertTrue(message.contains("macosMinimumDeploymentTarget.set(\"$SENTRY_COCOA_MIN_MACOS\")"))
        assertFalse(message.contains("tvosMinimumDeploymentTarget"))
        assertFalse(message.contains("watchosMinimumDeploymentTarget"))
        assertTrue(message.contains("set sentryKmp.autoInstall.apple.provider to SPM4KMP"))
        assertTrue(message.contains(SWIFTPM_IMPORT_DOCS))
        assertFalse(message.lines().any { it.trimStart().startsWith("|") })
    }

    private fun project(): Project =
        ProjectBuilder.builder().build().also {
            it.pluginManager.apply(KOTLIN_MULTIPLATFORM_PLUGIN_ID)
            it.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        }

    private fun spmFirstProject(): Project =
        ProjectBuilder.builder().build().also {
            it.pluginManager.apply(KOTLIN_MULTIPLATFORM_PLUGIN_ID)
            it.pluginManager.apply(SPM4KMP_PLUGIN_ID)
            it.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
            kotlin(it).iosArm64()
        }

    private fun options(project: Project) = project.extensions.getByType(SentryExtension::class.java).autoInstall

    private fun kotlin(project: Project) = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

    private fun swift(project: Project) = project.officialSwiftPmExtension() as SwiftPMImportExtension

    private fun configure(project: Project) = project.plugins.getPlugin(SentryPlugin::class.java).executeConfiguration(project)

    private fun SwiftPMDependency.Remote.exactVersion() = (version as SwiftPMDependency.Remote.Version.Exact).value

    private fun declarePackage(
        project: Project,
        product: String,
        url: String,
    ) {
        val swift = swift(project)
        swift.swiftPackage(swift.url(url), swift.exact("8.58.2"), listOf(swift.product(product)))
    }

    private companion object {
        const val COCOAPODS_PLUGIN_ID = "org.jetbrains.kotlin.native.cocoapods"
    }
}
