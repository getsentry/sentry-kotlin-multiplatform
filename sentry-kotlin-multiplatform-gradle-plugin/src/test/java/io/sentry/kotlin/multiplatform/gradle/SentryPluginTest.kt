package io.sentry.kotlin.multiplatform.gradle

import org.gradle.api.plugins.ExtensionAware
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

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
    fun `install Sentry pod when cocoapods plugin is applied and no custom Sentry configuration exists`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        project.afterEvaluate {
            val cocoapodsExtension = project.extensions.findByType(CocoapodsExtension::class.java)
            assertNotNull(cocoapodsExtension?.pods?.findByName("Sentry"))
        }
    }

    @Test
    fun `do not install Sentry pod when cocoapods plugin is applied and custom Sentry configuration exists`() {
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
}
