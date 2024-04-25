package com.ncorti.kotlin.gradle.template.plugin

import okhttp3.OkHttpClient
import okhttp3.Request
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.kpm.external.project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

const val EXTENSION_NAME = "templateExampleConfig"
const val TASK_NAME = "templateExample"

@Suppress("unused")
class TemplatePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        afterEvaluate {
            val extension = project.extensions.findByName("kotlin")
            if (extension !is KotlinMultiplatformExtension) {
                return@afterEvaluate
            }

            val nativeTargets = extension.targets.withType(KotlinNativeTarget::class.java)
            val targetNames = nativeTargets.map { target -> target.name }
            val buildDir = it.layout.buildDirectory.asFile.get().path

            extension.addLinkerOpts(buildDir)
            registerSentryDownloadTask()
            registerDownloadAndUnzipSentryTask()
            registerCopyFrameworkToTestDirsTask(targetNames)

            project.tasks.named { name -> name.contains("linkDebugTest") }.configureEach { task ->
                task.dependsOn("copyFrameworkToTestDirs")
            }
        }
    }
}

private fun Project.registerSentryDownloadTask() {
    tasks.register("downloadSentryFramework", Download::class.java) { download ->
        val sentryVersion = project.findProperty("sentryVersion") as String? ?: "8.21.0"
        val downloadPath = "${project.layout.buildDirectory.asFile.get().path}/sentry-temp"
        val zipFile = "$downloadPath/Sentry.xcframework.zip"

        download.src("https://github.com/getsentry/sentry-cocoa/releases/download/$sentryVersion/Sentry.xcframework.zip")
        download.dest(File(zipFile))
        download.overwrite(false)
    }
}

private fun Project.registerDownloadAndUnzipSentryTask() {
    tasks.register("downloadAndUnzipSentryFramework", Copy::class.java) { copy ->
        copy.dependsOn("downloadSentryFramework")
        val zipFile = "$buildDir/sentry-temp/Sentry.xcframework.zip"
        copy.from(zipTree(zipFile))
        copy.into("$buildDir/sentry-temp")
    }
}

private fun Project.registerCopyFrameworkToTestDirsTask(targetNames: List<String>) {
    tasks.register("copyFrameworkToTestDirs", Copy::class.java) { copy ->
        copy.dependsOn("downloadAndUnzipSentryFramework")
        targetNames.forEach { target ->
            val platform = when (target) {
                "iosSimulator64" -> "ios-arm64_x86_64-simulator"
                // Add other mappings as needed for different targets
                else -> null
            }
            if (platform != null) {
                val frameworkPath =
                    "$buildDir/sentry-temp/Carthage/Build/Sentry.xcframework/$platform/Sentry.framework"
                copy.from(project.fileTree(frameworkPath))
                copy.into("$buildDir/bin/${target}/debugTest/frameworks/Sentry.framework")
            }
        }
    }
}

private fun getFrameworkPath(buildDir: String, target: KotlinNativeTarget): String? {
    val platform = when (target.name) {
        "iosSimulator64" -> "ios-arm64_x86_64-simulator"
        else -> null
    }
    return platform?.let { "$buildDir/sentry-temp/Carthage/Build/Sentry.xcframework/$it/Sentry.framework" }
}

private fun KotlinMultiplatformExtension.addLinkerOpts(buildDir: String) {
    targets.withType(KotlinNativeTarget::class.java).forEach { target ->
        target.compilations.all {
            if (it.compilationName == "test" && it.target.platformType == KotlinPlatformType.native) {
                it.compilerOptions.configure {
                    freeCompilerArgs.add("-linker-options")
                    val platform: String? = when (target.name) {
                        "iosSimulatorArm64" -> "ios-arm64_x86_64-simulator"
                        else -> null
                    }
                    val path = "$buildDir/sentry-temp/Carthage/Build/Sentry.xcframework/$platform/"
                    freeCompilerArgs.add("-F$path")
                }
            }
        }
    }
}