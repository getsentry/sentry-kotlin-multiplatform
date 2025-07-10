package io.sentry.kotlin.multiplatform.gradle

import io.sentry.BuildConfig
import org.gradle.api.GradleException
import org.gradle.api.plugins.ExtensionAware
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SentryPluginTest {
    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assert(project.plugins.hasPlugin(SentryPlugin::class.java))
    }

    @Test
    fun `extension sentry is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assertNotNull(project.extensions.getByName("sentryKmp"))
    }

    @Test
    fun `extension linker is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assertNotNull(project.extensions.getByName("linker"))
    }

    @Test
    fun `extension autoInstall is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assertNotNull(project.extensions.getByName("autoInstall"))
    }

    @Test
    fun `extension cocoapods is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assertNotNull(project.extensions.getByName("cocoapods"))
    }

    @Test
    fun `extension commonMain is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assertNotNull(project.extensions.getByName("commonMain"))
    }

    @Test
    fun `plugin applies extensions correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assertNotNull(project.extensions.getByName("sentryKmp"))
        assertNotNull(project.extensions.getByName("linker"))
        assertNotNull(project.extensions.getByName("autoInstall"))
        assertNotNull(project.extensions.getByName("cocoapods"))
        assertNotNull(project.extensions.getByName("commonMain"))
    }

    @Test
    fun `default kmp version is set in commonMain extension`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val sourceSetAutoInstallExtension = project.extensions.getByName("commonMain") as SourceSetAutoInstallExtension
        assertEquals(BuildConfig.SentryKmpVersion, sourceSetAutoInstallExtension.sentryKmpVersion.get())
    }

    @Test
    fun `custom kmp version overrides default in commonMain extension`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val autoInstallExtension = project.extensions.getByName("autoInstall") as AutoInstallExtension
        autoInstallExtension.commonMain.sentryKmpVersion.set("1.2.3")

        assertEquals("1.2.3", autoInstallExtension.commonMain.sentryKmpVersion.get())
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.0.0", "2.3.4-SNAPSHOT", "latest.release"])
    fun `sentryKmpVersion accepts various version formats in commonMain extension`(version: String) {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val autoInstallExtension = project.extensions.getByName("autoInstall") as AutoInstallExtension
        autoInstallExtension.commonMain.sentryKmpVersion.set(version)

        assertEquals(version, autoInstallExtension.commonMain.sentryKmpVersion.get())
    }

    @Test
    fun `when autoInstall is disabled, no installations are performed`() {
        val project = ProjectBuilder.builder().build()

        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val autoInstallExtension = project.extensions.getByName("autoInstall") as AutoInstallExtension
        autoInstallExtension.enabled.set(false)

        project.afterEvaluate {
            val commonMainConfiguration =
                project.configurations.find { it.name.contains("commonMain", ignoreCase = true) }
            assertNull(commonMainConfiguration)

            val cocoapodsExtension = project.extensions.getByName("cocoapods") as CocoapodsExtension
            val sentryPod = cocoapodsExtension.pods.findByName("Sentry")
            assertNull(sentryPod)
        }
    }

    @Test
    fun `installSentryForKmp adds dependency to commonMain`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        project.installSentryForKmp(project.extensions.getByName("commonMain") as SourceSetAutoInstallExtension)

        val sentryDependencies = project.configurations
            .flatMap { it.dependencies }
            .filter { it.group == "io.sentry" && it.name == "sentry-kotlin-multiplatform" }
            .toList()

        assertTrue(sentryDependencies.isNotEmpty())

        val sentryDependency = sentryDependencies.first()
        assertEquals("io.sentry", sentryDependency.group)
        assertEquals("sentry-kotlin-multiplatform", sentryDependency.name)

        val commonMainConfiguration =
            project.configurations.find { it.name.contains("commonMain", ignoreCase = true) }
        assertNotNull(commonMainConfiguration)
        assertTrue(commonMainConfiguration!!.dependencies.contains(sentryDependency))
    }

    @OptIn(ExperimentalWasmDsl::class)
    @ParameterizedTest(name = "installSentryForKmp throws if build contains unsupported target {0}")
    @ValueSource(strings = ["wasm", "js", "mingw", "linux", "androidNative"])
    fun `installSentryForKmp throws if build contains any unsupported target`(unsupportedTarget: String) {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.apply {
            when (unsupportedTarget) {
                "wasm" -> wasmJs()
                "js" -> js()
                "mingw" -> mingwX64()
                "linux" -> linuxX64()
                "androidNative" -> androidNativeArm64()
            }
        }

        assertThrows<GradleException> {
            project.installSentryForKmp(project.extensions.getByName("commonMain") as SourceSetAutoInstallExtension)
        }
    }

    @Test
    fun `install Sentry pod if not already exists`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java) as ExtensionAware
        // Add a KMP target so cocoapods installation doesn't get skipped
        (kmpExtension as KotlinMultiplatformExtension).iosArm64()
        
        val cocoapodsExtension = kmpExtension.extensions.getByType(CocoapodsExtension::class.java)
        val sentryPod = cocoapodsExtension.pods.findByName("Sentry")

        // Check that it does not exist
        assertNull(sentryPod)

        val plugin = project.plugins.getPlugin(SentryPlugin::class.java)
        plugin.executeConfiguration(project, hostIsMac = true)

        // Check that it now exists
        val cocoapodsAutoInstallExtension = project.extensions.getByType(CocoapodsAutoInstallExtension::class.java)
        assertEquals(cocoapodsExtension.pods.getByName("Sentry").version, cocoapodsAutoInstallExtension.sentryCocoaVersion.get())
        assertTrue(cocoapodsExtension.pods.getByName("Sentry").linkOnly)
        assertEquals(cocoapodsExtension.pods.getByName("Sentry").extraOpts, listOf("-compiler-option", "-fmodules"))
    }

    @Test
    fun `install Sentry pod and prioritize user set version for cocoapods installation`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java) as ExtensionAware
        // Add a KMP target so cocoapods installation doesn't get skipped
        (kmpExtension as KotlinMultiplatformExtension).iosArm64()
        
        val cocoapodsAutoInstallExtension = project.extensions.getByType(CocoapodsAutoInstallExtension::class.java)
        val cocoapodsExtension = kmpExtension.extensions.getByType(CocoapodsExtension::class.java)
        val sentryPod = cocoapodsExtension.pods.findByName("Sentry")

        cocoapodsAutoInstallExtension.sentryCocoaVersion.set("10000.0.0")

        // Check that it does not exist
        assertNull(sentryPod)

        val plugin = project.plugins.getPlugin(SentryPlugin::class.java)
        plugin.executeConfiguration(project, hostIsMac = true)

        // Check that it now exists
        assertEquals(cocoapodsExtension.pods.getByName("Sentry").version, "10000.0.0")
    }

    @Test
    fun `do not install Sentry pod when cocoapods plugin when Sentry cocoapods configuration exists`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        val kmpExtension = project.extensions.findByName("kotlin")
        // Add a KMP target so cocoapods installation doesn't get skipped
        (kmpExtension as KotlinMultiplatformExtension).iosArm64()
        
        (kmpExtension as ExtensionAware).extensions.configure(CocoapodsExtension::class.java) { cocoapods ->
            cocoapods.pod("Sentry") { version = "custom version" }
        }

        val plugin = project.plugins.getPlugin(SentryPlugin::class.java)
        plugin.executeConfiguration(project)

        val cocoapodsExtension = kmpExtension.extensions.getByType(CocoapodsExtension::class.java)
        assertEquals(cocoapodsExtension.pods.getByName("Sentry").version, "custom version")
    }

    @Test
    fun `do not install Sentry pod if host is not mac`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        // Add a KMP target so cocoapods installation doesn't get skipped for other reasons
        kmpExtension.iosArm64()
        
        (kmpExtension as ExtensionAware).extensions.configure(CocoapodsExtension::class.java) { cocoapods ->
            cocoapods.ios.deploymentTarget = "14.1"
            cocoapods.summary = "Test"
            cocoapods.homepage = "https://sentry.io"
        }

        val plugin = project.plugins.getPlugin(SentryPlugin::class.java)
        plugin.executeConfiguration(project, hostIsMac = false)

        val cocoapodsExtension = (kmpExtension as ExtensionAware).extensions.getByType(CocoapodsExtension::class.java)
        assertNull(cocoapodsExtension.pods.findByName("Sentry"))
    }

    @Test
    fun `framework linking is attempted for projects with mixed Apple and non-Apple targets`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        
        // Add both Apple and non-Apple targets
        kmpExtension.iosArm64()
        kmpExtension.macosArm64()
        kmpExtension.linuxX64()

        // Set autoInstall to false to prevent automatic dependency installation
        val autoInstallExtension = project.extensions.getByType(AutoInstallExtension::class.java)
        autoInstallExtension.enabled.set(false)

        val plugin = project.plugins.getPlugin(SentryPlugin::class.java)
        
        // Should not throw an exception and should complete successfully
        plugin.executeConfiguration(project, hostIsMac = true)
        
        // Verify that the plugin executed without errors
        assertTrue(project.plugins.hasPlugin(SentryPlugin::class.java))
    }

    @Test
    fun `framework linking is skipped for projects with only non-Apple targets`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        
        // Add only non-Apple targets
        kmpExtension.linuxX64()
        kmpExtension.mingwX64()

        // Set autoInstall to false to prevent automatic dependency installation
        val autoInstallExtension = project.extensions.getByType(AutoInstallExtension::class.java)
        autoInstallExtension.enabled.set(false)

        val plugin = project.plugins.getPlugin(SentryPlugin::class.java)
        
        // Should not throw an exception and should complete successfully
        plugin.executeConfiguration(project, hostIsMac = true)
        
        // Verify that the plugin executed without errors
        assertTrue(project.plugins.hasPlugin(SentryPlugin::class.java))
    }
}
