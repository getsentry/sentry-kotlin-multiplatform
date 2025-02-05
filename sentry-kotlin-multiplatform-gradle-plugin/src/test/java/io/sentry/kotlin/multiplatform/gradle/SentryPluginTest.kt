package io.sentry.kotlin.multiplatform.gradle

import io.sentry.BuildConfig
import org.gradle.api.plugins.ExtensionAware
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
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

    @Test
    fun `install Sentry pod if not already exists`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        project.installSentryForCocoapods(project.extensions.getByName("cocoapods") as CocoapodsAutoInstallExtension)

        project.afterEvaluate {
            val cocoapodsExtension = project.extensions.findByType(CocoapodsExtension::class.java)
            val sentryPod = cocoapodsExtension?.pods?.findByName("Sentry")
            assertTrue(sentryPod != null)
            assertTrue(sentryPod!!.linkOnly)
        }
    }

    @Test
    fun `do not install Sentry pod when cocoapods plugin when Sentry cocoapods configuration exists`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        val kmpExtension = project.extensions.findByName("kotlin")
        (kmpExtension as ExtensionAware).extensions.configure(CocoapodsExtension::class.java) { cocoapods ->
            cocoapods.pod("Sentry") { version = "custom version" }
        }

        // plugin does not configure sentry pod if there is already an existing configuration
        project.afterEvaluate {
            val cocoapodsExtension = project.extensions.findByType(CocoapodsExtension::class.java)
            val pod = cocoapodsExtension?.pods?.findByName("Sentry")
            assertEquals(pod?.version, "custom version")
        }
    }
}
