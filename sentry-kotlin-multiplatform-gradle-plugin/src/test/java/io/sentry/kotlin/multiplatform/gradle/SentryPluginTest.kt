package io.sentry.kotlin.multiplatform.gradle

import io.github.frankois944.spmForKmp.swiftPackageConfig
import io.sentry.BuildConfig
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.ExtensionAware
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.konan.target.HostManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.net.URI

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
    fun `extension spm is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assertNotNull(project.extensions.getByName("spm"))
    }

    @Test
    fun `plugin applies extensions correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assertNotNull(project.extensions.getByName("sentryKmp"))
        assertNotNull(project.extensions.getByName("linker"))
        assertNotNull(project.extensions.getByName("autoInstall"))
        assertNotNull(project.extensions.getByName("cocoapods"))
        assertNotNull(project.extensions.getByName("spm"))
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

        val sentryDependencies =
            project.configurations
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

    @ParameterizedTest(name = "installSentryForKmp throws if build contains unsupported target {0}")
    @ValueSource(strings = ["androidNative"])
    fun `installSentryForKmp throws if build contains any unsupported target`(unsupportedTarget: String) {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.apply {
            when (unsupportedTarget) {
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
        val cocoapodsExtension = kmpExtension.extensions.getByType(CocoapodsExtension::class.java)
        val sentryPod = cocoapodsExtension.pods.findByName("Sentry")

        // Check that it does not exist
        assertNull(sentryPod)

        val plugin = project.plugins.getPlugin(SentryPlugin::class.java)
        plugin.executeConfiguration(project)

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

        val cocoapodsAutoInstallExtension = project.extensions.getByType(CocoapodsAutoInstallExtension::class.java)
        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java) as ExtensionAware
        val cocoapodsExtension = kmpExtension.extensions.getByType(CocoapodsExtension::class.java)
        val sentryPod = cocoapodsExtension.pods.findByName("Sentry")

        cocoapodsAutoInstallExtension.sentryCocoaVersion.set("10000.0.0")

        // Check that it does not exist
        assertNull(sentryPod)

        val plugin = project.plugins.getPlugin(SentryPlugin::class.java)
        plugin.executeConfiguration(project)

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
        (kmpExtension as ExtensionAware).extensions.configure(CocoapodsExtension::class.java) { cocoapods ->
            cocoapods.pod("Sentry") { version = "custom version" }
        }

        val plugin = project.plugins.getPlugin(SentryPlugin::class.java)
        plugin.executeConfiguration(project)

        val cocoapodsExtension = kmpExtension.extensions.getByType(CocoapodsExtension::class.java)
        assertEquals(cocoapodsExtension.pods.getByName("Sentry").version, "custom version")
        assertEquals("CocoaPods", project.externalCocoaFrameworkProvider())
    }

    @Test
    fun `default cocoa version is set in spm extension`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val spmExtension = project.extensions.getByName("spm") as Spm4KmpAutoInstallExtension
        assertEquals(BuildConfig.SentryCocoaVersion, spmExtension.sentryCocoaVersion.get())
    }

    @Test
    fun `custom cocoa version overrides default in spm extension`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val autoInstallExtension = project.extensions.getByName("autoInstall") as AutoInstallExtension
        autoInstallExtension.spm.sentryCocoaVersion.set("9.9.9")

        assertEquals("9.9.9", autoInstallExtension.spm.sentryCocoaVersion.get())
    }

    @Test
    fun `install Sentry Swift package via spm4Kmp when plugin is applied`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.iosArm64()

        val autoInstall = project.extensions.getByName("autoInstall") as AutoInstallExtension
        project.installSentryForSpm4Kmp(autoInstall, hostIsMac = true)

        // spm4Kmp keys per-target config as "<cinteropName>_<TargetCapitalized>" in its container.
        val swiftPackages =
            project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<*>
        assertNotNull(swiftPackages.findByName("${SENTRY_COCOA_CINTEROP_NAME}_IosArm64"))
        assertTrue(project.extensions.extraProperties.has(SPM_AUTO_INSTALLED_MARKER))
    }

    @Test
    fun `do not install Sentry Swift package when spm auto install is disabled`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val autoInstall = project.extensions.getByName("autoInstall") as AutoInstallExtension
        autoInstall.spm.enabled.set(false)

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.iosArm64()

        project.installSentryForSpm4Kmp(autoInstall, hostIsMac = true)

        val swiftPackages =
            project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<*>
        assertNull(swiftPackages.findByName("${SENTRY_COCOA_CINTEROP_NAME}_IosArm64"))
    }

    @Test
    fun `install Sentry Swift package when Kotlin Multiplatform plugin is applied last`() {
        Assumptions.assumeTrue(HostManager.hostIsMac)

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.iosArm64()

        val swiftPackages =
            project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<*>
        // Applied before spm4Kmp, so the install waits for afterEvaluate instead of running as the
        // target is created.
        assertNull(swiftPackages.findByName("${SENTRY_COCOA_CINTEROP_NAME}_IosArm64"))

        (project as ProjectInternal).evaluate()

        assertNotNull(swiftPackages.findByName("${SENTRY_COCOA_CINTEROP_NAME}_IosArm64"))
    }

    @Test
    fun `install Sentry Swift package eagerly when spm4Kmp is applied first`() {
        Assumptions.assumeTrue(HostManager.hostIsMac)

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.iosArm64()

        // spm4Kmp's afterEvaluate is queued ahead of ours, so the package has to be registered
        // while the target is being created.
        val swiftPackages =
            project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<*>
        assertNotNull(swiftPackages.findByName("${SENTRY_COCOA_CINTEROP_NAME}_IosArm64"))
    }

    @Test
    fun `spm opt-out after the kotlin block takes effect when applied before spm4Kmp`() {
        Assumptions.assumeTrue(HostManager.hostIsMac)

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.iosArm64()

        // Opting out after the targets exist, which the eager registration could not honor.
        val autoInstall = project.extensions.getByName("autoInstall") as AutoInstallExtension
        autoInstall.spm.enabled.set(false)

        (project as ProjectInternal).evaluate()

        val swiftPackages =
            project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<*>
        assertNull(swiftPackages.findByName("${SENTRY_COCOA_CINTEROP_NAME}_IosArm64"))
        assertFalse(project.extensions.extraProperties.has(SPM_AUTO_INSTALLED_MARKER))
    }

    @Test
    fun `user-defined per-target config is preserved when applied before spm4Kmp`() {
        Assumptions.assumeTrue(HostManager.hostIsMac)

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")

        // A config declared inside the target block, i.e. after the auto-install would have run
        // eagerly. Deferring to afterEvaluate lets it be detected instead of merged into.
        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.iosArm64().swiftPackageConfig(cinteropName = SENTRY_COCOA_CINTEROP_NAME) { }

        (project as ProjectInternal).evaluate()

        assertFalse(project.extensions.extraProperties.has(SPM_AUTO_INSTALLED_MARKER))
    }

    @Test
    fun `do not install Sentry Swift package when a user-defined per-target config exists`() {
        Assumptions.assumeTrue(HostManager.hostIsMac)

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")

        // The container is the only place the auto-install can detect this: no Kotlin cinterop
        // exists at configuration time.
        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.iosArm64().swiftPackageConfig(cinteropName = SENTRY_COCOA_CINTEROP_NAME) { }

        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assertFalse(project.extensions.extraProperties.has(SPM_AUTO_INSTALLED_MARKER))
        assertNull(project.externalCocoaFrameworkProvider())
    }

    @Test
    fun `do not install Sentry Swift package when a user-defined global config exists`() {
        Assumptions.assumeTrue(HostManager.hostIsMac)

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")

        // Old-style (non target-scoped) spm4Kmp config registered directly under "sentryCocoa".
        val swiftPackages =
            project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<*>
        swiftPackages.create(SENTRY_COCOA_CINTEROP_NAME)

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.iosArm64()

        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assertFalse(project.extensions.extraProperties.has(SPM_AUTO_INSTALLED_MARKER))
        // The config is left alone, but it covers no target on its own: without a matching cinterop
        // spm4Kmp never connects it to iosArm64, so that target still needs fallback linking.
        assertNull(project.externalCocoaFrameworkProvider())
        assertFalse(project.isSentryConfiguredViaSpm4Kmp("iosArm64"))
    }

    @Test
    fun `do not install Sentry Swift package when global auto install is disabled`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        val autoInstall = project.extensions.getByName("autoInstall") as AutoInstallExtension
        autoInstall.enabled.set(false)

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.iosArm64()

        project.installSentryForSpm4Kmp(autoInstall, hostIsMac = true)

        val swiftPackages =
            project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<*>
        assertNull(swiftPackages.findByName("${SENTRY_COCOA_CINTEROP_NAME}_IosArm64"))
    }

    @Test
    fun `spm4Kmp plugin without Sentry does not provide the framework`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.frankois944.spmForKmp")

        assertNull(project.externalCocoaFrameworkProvider())
    }

    @Test
    fun `CocoaPods plugin provides the framework even without a pod in the Kotlin DSL`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        // Sentry may be declared in the consumer's own Podfile, which never shows up in the
        // CocoaPods extension. Falling back to DerivedData linking here would throw for a project
        // that does have the framework.
        assertEquals("CocoaPods", project.externalCocoaFrameworkProvider())
    }

    @Test
    fun `target-specific spm4Kmp configuration does not provide the framework globally`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.iosArm64().swiftPackageConfig(cinteropName = SENTRY_COCOA_CINTEROP_NAME) { }

        assertNull(project.externalCocoaFrameworkProvider())
    }

    @Test
    fun `spm4Kmp configuration only covers its matching Apple target`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val configuredTarget =
            kmpExtension.iosArm64().also {
                it.swiftPackageConfig(cinteropName = SENTRY_COCOA_CINTEROP_NAME) { }
            }
        val fallbackTarget = kmpExtension.iosSimulatorArm64()

        assertTrue(project.isSentryConfiguredViaSpm4Kmp(configuredTarget.name))
        assertFalse(project.isSentryConfiguredViaSpm4Kmp(fallbackTarget.name))
    }

    @Test
    fun `do not install Sentry pod if host is not mac`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
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
    fun `global spm4Kmp config only covers targets that declare the cinterop`() {
        Assumptions.assumeTrue(HostManager.hostIsMac)

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")

        val swiftPackages =
            project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<*>
        swiftPackages.create(SENTRY_COCOA_CINTEROP_NAME)

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val device = kmpExtension.iosArm64()
        val simulator = kmpExtension.iosSimulatorArm64()
        // spm4Kmp connects a global config to targets through matching cinterop tasks, so a target
        // without the cinterop is not covered by it and still needs fallback linking.
        device.compilations
            .getByName("main")
            .cinterops
            .create(SENTRY_COCOA_CINTEROP_NAME)

        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")

        assertNull(project.externalCocoaFrameworkProvider())
        assertTrue(project.isSentryConfiguredViaSpm4Kmp(device.name))
        assertFalse(project.isSentryConfiguredViaSpm4Kmp(simulator.name))
    }

    @Test
    fun `do not add a competing Sentry package when another target is user-configured`() {
        Assumptions.assumeTrue(HostManager.hostIsMac)

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.sentry.kotlin.multiplatform.gradle")
        project.pluginManager.apply("io.github.frankois944.spmForKmp")
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")

        val kmpExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kmpExtension.iosArm64()
        val simulator = kmpExtension.iosSimulatorArm64()
        simulator.swiftPackageConfig(cinteropName = SENTRY_COCOA_CINTEROP_NAME) {
            dependency {
                remotePackageVersion(
                    url = URI("https://github.com/getsentry/sentry-cocoa.git"),
                    version = "8.57.0",
                    products = { add("Sentry") },
                )
            }
        }

        (project as ProjectInternal).evaluate()

        val swiftPackages =
            project.extensions.getByName("swiftPackageConfig") as NamedDomainObjectContainer<*>
        // spm4Kmp merges configs sharing a cinterop name into a single package, so auto-installing
        // the default version alongside the pinned 8.57.0 would silently replace it.
        assertNull(swiftPackages.findByName("${SENTRY_COCOA_CINTEROP_NAME}_IosArm64"))
        assertNotNull(swiftPackages.findByName("${SENTRY_COCOA_CINTEROP_NAME}_IosSimulatorArm64"))
    }
}
