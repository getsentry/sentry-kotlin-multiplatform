package com.ncorti.kotlin.gradle.template.plugin

import okhttp3.OkHttpClient
import okhttp3.Request
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.kpm.external.project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
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
            if (extension is KotlinMultiplatformExtension) {
                extension.targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java).forEach {
                    it.compilations.all {
                        if (it.compilationName == "test" && it.target.platformType == KotlinPlatformType.native) {
                            it.compilerOptions.configure {
                                freeCompilerArgs.add("-linker-options")
                                val path = "${project.buildDir}/sentry-temp/Carthage/Build/Sentry.xcframework/ios-arm64_x86_64-simulator/"
                                print(path)
                                freeCompilerArgs.add("-F$path")
                            }
                        }
                    }
                }
                it.tasks.register("downloadSentry", Download::class.java) { download ->
                    // TODO: only if it doesn't exist or give the option to overwrite
                    val sentryVersion = project.findProperty("sentryVersion") as String? ?: "8.21.0"
                    val downloadPath = "${project.layout.buildDirectory.asFile.get().path}/sentry-temp"
                    val zipFile = "$downloadPath/Sentry.xcframework.zip"

                    println(downloadPath)
                    download.src("https://github.com/getsentry/sentry-cocoa/releases/download/$sentryVersion/Sentry.xcframework.zip")
                    download.dest(File(zipFile))
                    download.overwrite(true)

                    println("finished downloading Sentry")
                }

                it.tasks.register("setupSentry", Copy::class.java) { copy ->
                    copy.dependsOn("downloadSentry")
                    val zipFile = "${project.buildDir}/sentry-temp/Sentry.xcframework.zip"
                    copy.from(zipTree(zipFile))
                    copy.into("${project.buildDir}/sentry-temp")

                    copy.from(project.fileTree("${project.buildDir}/sentry-temp/Carthage/Build/Sentry.xcframework/ios-arm64_x86_64-simulator/Sentry.framework"))
                    copy.into("${project.buildDir}/bin/iosSimulatorArm64/debugTest/frameworks/Sentry.framework")
                }

                project.tasks.named("linkDebugTestIosSimulatorArm64").configure { task ->
                    task.dependsOn("setupSentry")
                }
            }
        }
    }
}

private fun KotlinMultiplatformExtension.downloadAndSetupSentry() {
    targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java).forEach {
    }
}

private fun configureDownloadTask(project: Project) {
    val sentryVersion = project.findProperty("sentryVersion") as String? ?: "8.21.0"
    val downloadPath = "${project.layout.buildDirectory.asFile.get().path}/sentry-temp"
    val zipFile = "$downloadPath/Sentry.xcframework.zip"

//    project.tasks.register<Download>("downloadSentry") {
//        group = "build setup"
//        description = "Download Sentry framework."
//        src("https://github.com/getsentry/sentry-cocoa/releases/download/$sentryVersion/Sentry.xcframework.zip")
//        dest(File(zipFile))
//        overwrite(true)
//    }
}
