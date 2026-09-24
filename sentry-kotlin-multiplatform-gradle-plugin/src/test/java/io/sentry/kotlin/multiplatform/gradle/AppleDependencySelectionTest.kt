@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

package io.sentry.kotlin.multiplatform.gradle

import io.sentry.BuildConfig
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.ExtensionAware
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.swiftimport.SwiftPMDependency
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.swiftimport.SwiftPMImportExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
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
    @Test
    fun `empty official extension does not select SwiftPM`() {
        val project = project()
        assertNotNull(project.officialSwiftPmExtension())
        assertEquals(AppleDependencyProvider.NONE, project.resolveAppleDependencyProvider(options(project)))
        project.pluginManager.apply(SPM4KMP_PLUGIN_ID)
        assertEquals(AppleDependencyProvider.SPM4KMP, project.resolveAppleDependencyProvider(options(project)))
    }

    @Test
    fun `AUTO prefers integrations in use and honors legacy disables`() {
        val project = project()
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")
        assertEquals(AppleDependencyProvider.COCOAPODS, project.resolveAppleDependencyProvider(options(project)))
        project.pluginManager.apply(SPM4KMP_PLUGIN_ID)
        assertEquals(AppleDependencyProvider.SPM4KMP, project.resolveAppleDependencyProvider(options(project)))
        options(project).spm.enabled.set(false)
        assertEquals(AppleDependencyProvider.COCOAPODS, project.resolveAppleDependencyProvider(options(project)))
        options(project).cocoapods.enabled.set(false)
        assertEquals(AppleDependencyProvider.NONE, project.resolveAppleDependencyProvider(options(project)))
        declarePackage(project, "Other", "https://example.com/other.git")
        assertEquals(AppleDependencyProvider.SWIFT_PM, project.resolveAppleDependencyProvider(options(project)))
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

    @ParameterizedTest
    @EnumSource(value = AppleDependencyProvider::class, names = ["SPM4KMP", "COCOAPODS"])
    fun `unavailable explicit provider fails without fallback`(provider: AppleDependencyProvider) {
        val project = project()
        declarePackage(project)
        options(project).apple.provider.set(provider)
        val error = assertThrows(GradleException::class.java) { project.resolveAppleDependencyProvider(options(project)) }
        assertTrue(error.message!!.contains("$provider is unavailable"))
    }

    @Test
    fun `NONE keeps commonMain and manual official framework coverage`() {
        val project = project()
        val ios = kotlin(project).iosSimulatorArm64()
        declarePackage(project)
        val before = swift(project).swiftPMDependencies.toList()
        options(project).apple.provider.set(AppleDependencyProvider.NONE)
        configure(project)
        assertEquals(before, swift(project).swiftPMDependencies.toList())
        assertTrue(
            project.configurations
                .getByName("commonMainApi")
                .dependencies
                .any { it.name == "sentry-kotlin-multiplatform" },
        )
        assertTrue(project.isSentryConfiguredViaOfficialSwiftPm(ios))
    }

    @Test
    fun `NONE does not register any Apple package`() {
        val project = project()
        project.pluginManager.apply(SPM4KMP_PLUGIN_ID)
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")
        kotlin(project).iosArm64()
        options(project).apple.provider.set(AppleDependencyProvider.NONE)
        configure(project)
        assertTrue(swift(project).swiftPMDependencies.isEmpty())
        assertFalse(project.isSentryConfiguredViaSpm4Kmp("iosArm64"))
        val pods = (kotlin(project) as ExtensionAware).extensions.getByType(CocoapodsExtension::class.java)
        assertNull(pods.pods.findByName("Sentry"))
    }

    @Test
    fun `explicit spm overrides official dependency detection`() {
        val project = project()
        project.pluginManager.apply(SPM4KMP_PLUGIN_ID)
        kotlin(project).iosArm64()
        declarePackage(project, "Other", "https://example.com/other.git")
        options(project).apple.provider.set(AppleDependencyProvider.SPM4KMP)
        configure(project)
        assertTrue(project.isSentryConfiguredViaSpm4Kmp("iosArm64"))
        assertFalse(OfficialSwiftPmIntegration.hasSentry(swift(project)))
    }

    @Test
    fun `explicit disabled provider does not fall back`() {
        val project = project()
        project.pluginManager.apply(SPM4KMP_PLUGIN_ID)
        declarePackage(project)
        options(project).apple.provider.set(AppleDependencyProvider.SPM4KMP)
        options(project).spm.enabled.set(false)
        assertEquals(AppleDependencyProvider.NONE, project.resolveAppleDependencyProvider(options(project)))
    }

    @Test
    fun `official installer pins Cocoa and preserves higher platform minimums`() {
        val project = project()
        kotlin(project).apply {
            iosArm64()
            macosArm64()
            tvosArm64()
            watchosArm64()
        }
        options(project).apple.provider.set(AppleDependencyProvider.SWIFT_PM)
        swift(project).iosMinimumDeploymentTarget.set("16.1")
        swift(project).macosMinimumDeploymentTarget.set("11.0")
        configure(project)
        val dependency = swift(project).swiftPMDependencies.single() as SwiftPMDependency.Remote
        assertEquals(BuildConfig.SentryCocoaVersion, (dependency.version as SwiftPMDependency.Remote.Version.Exact).value)
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
    fun `manual Sentry declaration version and platform restrictions are preserved`() {
        val project = project()
        val ios = kotlin(project).iosArm64()
        val mac = kotlin(project).macosArm64()
        val swift = swift(project)
        swift.swiftPackage(
            swift.url("https://github.com/getsentry/sentry-cocoa.git"),
            swift.exact("8.58.2"),
            listOf(swift.product("Sentry", setOf(swift.iOS()))),
        )
        val before = swift.swiftPMDependencies.toList()
        configure(project)
        assertEquals(before, swift.swiftPMDependencies.toList())
        assertTrue(project.isSentryConfiguredViaOfficialSwiftPm(ios))
        assertFalse(project.isSentryConfiguredViaOfficialSwiftPm(mac))
    }

    @Test
    fun `Sentry repository without Sentry product is not duplicated or treated as coverage`() {
        val project = project()
        val ios = kotlin(project).iosArm64()
        val swift = swift(project)
        swift.swiftPackage(swift.url("https://github.com/getsentry/sentry-cocoa.git"), swift.exact("8.58.2"), emptyList())
        configure(project)
        assertEquals(1, swift(project).swiftPMDependencies.size)
        assertFalse(project.isSentryConfiguredViaOfficialSwiftPm(ios))
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
    fun `official provider does not require spm ordering when spm is applied first`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(KOTLIN_MULTIPLATFORM_PLUGIN_ID)
        project.pluginManager.apply(SPM4KMP_PLUGIN_ID)
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        kotlin(project).iosArm64()
        declarePackage(project, "Other", "https://example.com/other.git")
        SentryPlugin().executeConfiguration(project, hostIsMac = true, spmAppliedFirst = true)
        assertTrue(OfficialSwiftPmIntegration.hasSentry(swift(project)))
        assertFalse(project.isSentryConfiguredViaSpm4Kmp("iosArm64"))
    }

    @Test
    fun `after evaluation registration sees all user declarations`() {
        val project = project()
        kotlin(project).iosArm64()
        declarePackage(project, "Other", "https://example.com/other.git")
        (project as ProjectInternal).evaluate()
        assertTrue(OfficialSwiftPmIntegration.hasSentry(swift(project)))
        assertEquals(2, swift(project).swiftPMDependencies.size)
    }

    @ParameterizedTest
    @CsvSource(
        "false,false,false,NONE",
        "true,false,false,SWIFT_PM",
        "false,true,false,SPM4KMP",
        "false,false,true,COCOAPODS",
        "true,true,false,SWIFT_PM",
        "true,false,true,SWIFT_PM",
        "false,true,true,SPM4KMP",
        "true,true,true,SWIFT_PM",
    )
    fun `AUTO selects the highest priority active provider`(
        official: Boolean,
        spm: Boolean,
        pods: Boolean,
        expected: AppleDependencyProvider,
    ) {
        val project = project()
        if (spm) project.pluginManager.apply(SPM4KMP_PLUGIN_ID)
        if (pods) project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")
        if (official) declarePackage(project, "Other", "https://example.com/other.git")
        assertEquals(expected, project.resolveAppleDependencyProvider(options(project)))
    }

    @ParameterizedTest
    @ValueSource(strings = ["Sentry-Dynamic", "SentrySwiftUI", "Sentry-WithoutUIKitOrAppKit"])
    fun `manual alternative Sentry products provide framework coverage`(product: String) {
        val project = project()
        val ios = kotlin(project).iosArm64()
        declarePackage(project, product)
        options(project).apple.provider.set(AppleDependencyProvider.NONE)
        configure(project)
        assertTrue(project.isSentryConfiguredViaOfficialSwiftPm(ios))
        assertEquals(1, swift(project).swiftPMDependencies.size)
    }

    private fun project(): Project =
        ProjectBuilder.builder().build().also {
            it.pluginManager.apply(KOTLIN_MULTIPLATFORM_PLUGIN_ID)
            it.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        }

    private fun options(project: Project) = project.extensions.getByType(SentryExtension::class.java).autoInstall

    private fun kotlin(project: Project) = project.extensions.getByType(KotlinMultiplatformExtension::class.java)

    private fun swift(project: Project) = project.officialSwiftPmExtension() as SwiftPMImportExtension

    private fun configure(project: Project) = project.plugins.getPlugin(SentryPlugin::class.java).executeConfiguration(project)

    private fun declarePackage(
        project: Project,
        product: String = "Sentry",
        url: String = "https://github.com/getsentry/sentry-cocoa.git",
    ) {
        val swift = swift(project)
        swift.swiftPackage(swift.url(url), swift.exact("8.58.2"), listOf(swift.product(product)))
    }
}
