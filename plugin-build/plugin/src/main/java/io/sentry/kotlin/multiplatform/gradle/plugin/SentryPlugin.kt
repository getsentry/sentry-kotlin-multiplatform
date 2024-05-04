package io.sentry.kotlin.multiplatform.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.Copy
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.io.File

const val EXTENSION_NAME = "sentry"
const val COCOAPODS_PLUGIN_NAME = "org.jetbrains.kotlin.plugin.cocoapods"
const val KOTLIN_EXTENSION_NAME = "kotlin"

@Suppress("unused")
class SentryPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        val extension =
            project.extensions.create(EXTENSION_NAME, SentryExtension::class.java, project)
        afterEvaluate {
            val hasCocoapodsPlugin = project.pluginManager.hasPlugin(COCOAPODS_PLUGIN_NAME)
            if (hasCocoapodsPlugin) {
                setupCocoapods()
            } else {
                if (extension.enableSentryTestLinking.get()) {
                    // Test linking is only necessary when NOT using the Kotlin Cocoapods plugin
                    setupSentryFrameworkForTests()
                }
            }
        }
    }

    /**
     * Installs the Sentry pod if not yet installed and configures the compiler options
     */
    private fun Project.setupCocoapods() {
        val kmpExtension = this.extensions.findByName(KOTLIN_EXTENSION_NAME)
        if (kmpExtension !is KotlinMultiplatformExtension) {
            return
        }

        (kmpExtension as ExtensionAware).extensions.configure(CocoapodsExtension::class.java) { cocoapods ->
            val sentryPod = cocoapods.pods.findByName("Sentry")
            if (sentryPod == null) {
                cocoapods.pod("Sentry") {
                    version = "8.25.0"
                    extraOpts += listOf("-compiler-option", "-fmodules")
                }
            } else {
                if (!sentryPod.extraOpts.contains("-fmodules")) {
                    sentryPod.extraOpts += listOf("-compiler-option", "-fmodules")
                }
            }
        }
    }

    private fun Project.setupSentryFrameworkForTests() {
        val kmpExtension = this.extensions.findByName(KOTLIN_EXTENSION_NAME)
        if (kmpExtension !is KotlinMultiplatformExtension) {
            return
        }

        val appleTargets = kmpExtension.targets.withType(KotlinNativeTarget::class.java)
            .filter { it.konanTarget.family.isAppleFamily }
        val buildDir = layout.buildDirectory.asFile.get().path

        kmpExtension.addLinkerOpts(buildDir)
        registerSentryDownloadTask()
        registerDownloadAndUnzipSentryTask()
        registerCopyFrameworkToTestDirsTask(appleTargets)

        tasks.named { name -> name.contains("linkDebugTest") }.configureEach { task ->
            task.dependsOn("copyFrameworkToTestDirs")
        }
    }

    private fun Project.registerSentryDownloadTask() {
        val buildDir = layout.buildDirectory.asFile.get().path
        tasks.register("downloadSentryFramework", Download::class.java) { download ->
            val sentryVersion = project.findProperty("sentryVersion") as String? ?: "8.25.0"
            val downloadPath = "$buildDir/sentry-temp"
            val zipFile = "$downloadPath/Sentry.xcframework.zip"

            download.src("https://github.com/getsentry/sentry-cocoa/releases/download/$sentryVersion/Sentry.xcframework.zip")
            download.dest(File(zipFile))
            download.overwrite(false)
        }
    }

    private fun Project.registerDownloadAndUnzipSentryTask() {
        val buildDir = layout.buildDirectory.asFile.get().path
        tasks.register("downloadAndUnzipSentryFramework", Copy::class.java) { copy ->
            copy.dependsOn("downloadSentryFramework")
            val zipFile = "$buildDir/sentry-temp/Sentry.xcframework.zip"
            copy.from(zipTree(zipFile))
            copy.into("$buildDir/sentry-temp")
        }
    }

    private fun Project.registerCopyFrameworkToTestDirsTask(targets: List<KotlinNativeTarget>) {
        val buildDir = layout.buildDirectory.asFile.get().path
        tasks.register("copyFrameworkToTestDirs", Copy::class.java) { copy ->
            copy.dependsOn("downloadAndUnzipSentryFramework")

            targets.forEach { target ->
                val frameworkPath =
                    "$buildDir/sentry-temp/Carthage/Build/Sentry.xcframework/${target.xcFrameworkDir()}/Sentry.framework"
                copy {
                    it.from(fileTree(frameworkPath))
                    it.into("$buildDir/bin/${target.name}/debugTest/frameworks/Sentry.framework")
                }
            }
        }
    }

    private fun KotlinNativeTarget.xcFrameworkDir(): String? {
        return when (name) {
            "iosSimulatorArm64" -> "ios-arm64_x86_64-simulator"
            "iosX64" -> "ios-arm64_x86_64-simulator"
            "iosArm64" -> "ios-arm64"
            else -> null
        }
    }

    private fun KotlinMultiplatformExtension.addLinkerOpts(buildDir: String) {
        targets.withType(KotlinNativeTarget::class.java).forEach { target ->
            target.compilations.all {
                if (it.compilationName == "test" && it.target.platformType == KotlinPlatformType.native) {
                    it.compilerOptions.configure {
                        freeCompilerArgs.add("-linker-options")
                        val path =
                            "$buildDir/sentry-temp/Carthage/Build/Sentry.xcframework/${target.xcFrameworkDir()}/"
                        freeCompilerArgs.add("-F$path")
                    }
                }
            }
        }
    }
}