package io.sentry.kotlin.multiplatform.gradle.plugin

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import java.io.ByteArrayOutputStream
import java.io.File

const val SENTRY_EXTENSION_NAME = "sentry"
const val LINKER_EXTENSION_NAME = "linker"
const val COCOAPODS_PLUGIN_NAME = "org.jetbrains.kotlin.plugin.cocoapods"
const val KOTLIN_EXTENSION_NAME = "kotlin"

@Suppress("unused")
class SentryPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        val extension =
            project.extensions.create(SENTRY_EXTENSION_NAME, SentryExtension::class.java, project)
        project.extensions.add(LINKER_EXTENSION_NAME, extension.linker)

        afterEvaluate {
            val hasCocoapodsPlugin = project.pluginManager.hasPlugin(COCOAPODS_PLUGIN_NAME)
            if (hasCocoapodsPlugin) {
                setupCocoapods()
            } else {
                configureLinkingOptions(extension.linker)
            }
        }
    }
}

private fun Project.configureLinkingOptions(linkerExtension: LinkerExtension) {
    val kmpExtension = this.extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension) {
        // todo: log, not multiplatform found
        return
    }

    val customXcodeprojPath = linkerExtension.xcodeprojPath.orNull
    println("customXcodeprojPath: $customXcodeprojPath")

    val derivedDataPath = findDerivedDataPath(customXcodeprojPath)

    kmpExtension.appleTargets().all { target ->
        val frameworkArchitecture = target.frameworkArchitecture()
        val dynamicFrameworkPath =
            "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry-Dynamic/Sentry-Dynamic.xcframework/$frameworkArchitecture"
        val staticFrameworkPath =
            "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry/Sentry.xcframework/$frameworkArchitecture"

        target.binaries.all { binary ->
            if (frameworkArchitecture == null) {
                // todo: log, unsupported architecture
                return@all
            }

            var path = dynamicFrameworkPath
            if (!File(dynamicFrameworkPath).exists()) {
                // if it doesn't exist still search for the static one since we need either one
                // for test linking
                // todo: log, dynamic framework not found, using static framework
                path = staticFrameworkPath

                if (!File(staticFrameworkPath).exists()) {
                    // todo: log, static framework also not found, error
                    return@all
                }
            }

            val testExecutable = binary is TestExecutable
            val dynamicFramework = binary is Framework && !binary.isStatic
            if (testExecutable) {
                binary.linkerOpts("-rpath", "$path/Sentry.framework")
                binary.linkerOpts("-F$path")
            } else if (dynamicFramework) {
                binary.linkerOpts("-F$path")
            }
        }
    }
}

private fun Project.findDerivedDataPath(customXcodeprojPath: String? = null): String {
    val xcodeprojPath = customXcodeprojPath ?: findXcodeprojFile(rootDir)?.absolutePath
    val buildDirOutput = ByteArrayOutputStream()
    exec {
        it.commandLine = listOf(
            "xcodebuild", "-project", xcodeprojPath, "-showBuildSettings"
        )
        it.standardOutput = buildDirOutput
    }
    val buildSettings = buildDirOutput.toString("UTF-8")
    val buildDirRegex = Regex("BUILD_DIR = (.+)")
    val buildDirMatch = buildDirRegex.find(buildSettings)
    val buildDir = buildDirMatch?.groupValues?.get(1)
        ?: throw GradleException("BUILD_DIR not found in xcodebuild output")
    val derivedDataPath = buildDir.replace("/Build/Products", "")
    return derivedDataPath
}

private fun findXcodeprojFile(dir: File): File? {
    val ignoredDirectories = listOf("build", "DerivedData")

    fun searchDirectory(directory: File): File? {
        val files = directory.listFiles() ?: return null

        for (file in files) {
            if (file.name in ignoredDirectories) {
                continue
            }

            // Recursively search the subdirectory
            val result = searchDirectory(file)
            if (result != null) {
                return result
            }

            if (file.extension == "xcodeproj") {
                return file
            }
        }
        return null
    }

    return searchDirectory(dir)
}

private fun KotlinMultiplatformExtension.appleTargets() =
    targets.withType(KotlinNativeTarget::class.java)
        .matching { it.konanTarget.family.isAppleFamily }

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
                linkOnly = true
                extraOpts += listOf("-compiler-option", "-fmodules")
            }
        }
    }
}

/**
 * Transforms a Kotlin Multiplatform target name to the architecture name that is found inside
 * Sentry's framework directory.
 */
private fun KotlinNativeTarget.frameworkArchitecture(): String? {
    return when (name) {
        "iosSimulatorArm64" -> "ios-arm64_x86_64-simulator"
        "iosX64" -> "ios-arm64_x86_64-simulator"
        "iosArm64" -> "ios-arm64"
        else -> null
    }
}
