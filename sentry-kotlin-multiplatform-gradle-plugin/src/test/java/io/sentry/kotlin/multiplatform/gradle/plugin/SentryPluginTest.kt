package io.sentry.kotlin.multiplatform.gradle.plugin

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.gradle.api.plugins.ExtensionAware
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class SentryPluginTest {
    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle.plugin")

        assert(project.plugins.hasPlugin(SentryPlugin::class.java))
    }

    @Test
    fun `extension sentry is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle.plugin")

        assertNotNull(project.extensions.getByName("sentry"))
    }

    @Test
    fun `extension linker is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle.plugin")

        assertNotNull(project.extensions.getByName("linker"))
    }

    @Test
    fun `frameworkArchitecture returns transformed target with supported targets`() {
        val target = mock(KotlinNativeTarget::class.java)

        `when`(target.name).thenReturn("iosSimulatorArm64")
        assertEquals("ios-arm64_x86_64-simulator", target.toSentryFrameworkArchitecture())

        `when`(target.name).thenReturn("iosX64")
        assertEquals("ios-arm64_x86_64-simulator", target.toSentryFrameworkArchitecture())

        `when`(target.name).thenReturn("iosArm64")
        assertEquals("ios-arm64", target.toSentryFrameworkArchitecture())
    }

    @Test
    fun `frameworkArchitecture returns null with unsupported targets`() {
        val target = mock(KotlinNativeTarget::class.java)

        `when`(target.name).thenReturn("unsupported")
        assertNull(target.toSentryFrameworkArchitecture())
    }

    @Test
    fun `install Sentry pod when cocoapods plugin is applied and no custom Sentry configuration exists`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle.plugin")
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
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle.plugin")
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        val kmpExtension = project.extensions.findByName("kotlin")
        (kmpExtension as ExtensionAware).extensions.configure(CocoapodsExtension::class.java) { cocoapods ->
            cocoapods.pod("Sentry") {
                version = "custom version"
            }
        }

        // plugin does not configure sentry pod if there is already an existing configuration
        project.afterEvaluate {
            val cocoapodsExtension = project.extensions.findByType(CocoapodsExtension::class.java)
            val pod = cocoapodsExtension?.pods?.findByName("Sentry")
            assertEquals(pod?.version, "custom version")
        }
    }
}
