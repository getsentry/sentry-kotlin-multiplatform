package io.sentry.kotlin.multiplatform.gradle.plugin

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.KotlinCocoapodsPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import java.io.ByteArrayOutputStream
import java.io.File

internal const val SENTRY_EXTENSION_NAME = "sentry"
internal const val LINKER_EXTENSION_NAME = "linker"
internal const val AUTO_INSTALL_EXTENSION_NAME = "autoInstall"
internal const val COCOAPODS_AUTO_INSTALL_EXTENSION_NAME = "cocoapods"
internal const val COMMON_MAIN_AUTO_INSTALL_EXTENSION_NAME = "commonMain"
internal const val KOTLIN_EXTENSION_NAME = "kotlin"

@Suppress("unused")
class SentryPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit =
        with(project) {
            val sentryExtension =
                project.extensions.create(
                    SENTRY_EXTENSION_NAME,
                    SentryExtension::class.java,
                    project,
                )
            project.extensions.add(LINKER_EXTENSION_NAME, sentryExtension.linker)
            project.extensions.add(AUTO_INSTALL_EXTENSION_NAME, sentryExtension.autoInstall)
            project.extensions.add(
                COCOAPODS_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.cocoapods,
            )
            project.extensions.add(
                COMMON_MAIN_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.commonMain,
            )

            afterEvaluate {
                val hasCocoapodsPlugin =
                    project.plugins.findPlugin(KotlinCocoapodsPlugin::class.java) != null

                if (sentryExtension.autoInstall.enabled.get()) {
                    if (sentryExtension.autoInstall.commonMain.enabled.get()) {
                        installSentryForKmp(sentryExtension.autoInstall.commonMain)
                    }
                    if (hasCocoapodsPlugin && sentryExtension.autoInstall.cocoapods.enabled.get()) {
                        installSentryForCocoapods(sentryExtension.autoInstall.cocoapods)
                    }
                }

                // If the user is not using the cocoapods plugin, linking to the framework is not automatic
                // so we have to take care of that
                if (!hasCocoapodsPlugin) {
                    configureLinkingOptions(sentryExtension.linker)
                }
            }
        }
}

internal fun Project.installSentryForKmp(commonMainAutoInstallExtension: SourceSetAutoInstallExtension) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension) {
        // todo: log, not multiplatform found
        return
    }

    val unsupportedTargets = listOf("wasm", "js", "mingw", "linux")
    kmpExtension.targets.forEach { target ->
        if (unsupportedTargets.any { unsupported ->
                target.name.contains(unsupported)
            }
        ) {
            throw GradleException(
                "Unsupported target: ${target.name}. " +
                    "Cannot auto install in commonMain. " +
                    "Please create an intermediate sourceSet with targets that the Sentry SDK supports (apple, jvm, android) and add the dependency manually.",
            )
        }
    }

    val commonMain =
        kmpExtension.sourceSets.find {
            it.name.contains("common")
        }

    val sentryVersion = commonMainAutoInstallExtension.sentryKmpVersion.get()
    commonMain?.dependencies {
        api("io.sentry:sentry-kotlin-multiplatform:$sentryVersion")
    }
}

internal fun Project.installSentryForCocoapods(cocoapodsAutoInstallExtension: CocoapodsAutoInstallExtension) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension) {
        return
    }

    (kmpExtension as ExtensionAware).extensions.configure(CocoapodsExtension::class.java) { cocoapods ->
        val podName = "Sentry"
        val sentryPod = cocoapods.pods.findByName(podName)
        if (sentryPod == null) {
            cocoapods.pod(podName) {
                version = cocoapodsAutoInstallExtension.sentryCocoaVersion.get()
                linkOnly = true
                extraOpts += listOf("-compiler-option", "-fmodules")
            }
        }
    }
}

internal fun Project.configureLinkingOptions(linkerExtension: LinkerExtension) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension) {
        // todo: log, not multiplatform found
        return
    }

    val customXcodeprojPath = linkerExtension.xcodeprojPath.orNull
    val derivedDataPath = findDerivedDataPath(customXcodeprojPath)

    kmpExtension.appleTargets().all { target ->
        val frameworkArchitecture = target.toSentryFrameworkArchitecture()
        if (frameworkArchitecture == null) {
            // todo: log, unsupported architecture
            return@all
        }

        val dynamicFrameworkPath =
            "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry-Dynamic/Sentry-Dynamic.xcframework/$frameworkArchitecture"
        val staticFrameworkPath =
            "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry/Sentry.xcframework/$frameworkArchitecture"

        target.binaries.all binaries@{ binary ->
            val path =
                when {
                    File(dynamicFrameworkPath).exists() -> dynamicFrameworkPath
                    File(staticFrameworkPath).exists() -> {
                        // todo: log, dynamic framework not found, try using static framework
                        staticFrameworkPath
                    }

                    else -> {
                        // todo: log, static framework also not found, error
                        return@binaries
                    }
                }

            if (binary is TestExecutable) {
                binary.linkerOpts("-rpath", "$path/Sentry.framework", "-F$path")
            } else if (binary is Framework) {
                binary.linkerOpts("-F$path")
            }
        }
    }
}

private fun Project.findDerivedDataPath(customXcodeprojPath: String? = null): String {
    val xcodeprojPath = customXcodeprojPath ?: findXcodeprojFile(rootDir)?.absolutePath
    val buildDirOutput = ByteArrayOutputStream()
    exec {
        it.commandLine =
            listOf(
                "xcodebuild", "-project", xcodeprojPath, "-showBuildSettings",
            )
        it.standardOutput = buildDirOutput
    }
    val buildSettings = buildDirOutput.toString("UTF-8")
    val buildDirRegex = Regex("BUILD_DIR = (.+)")
    val buildDirMatch = buildDirRegex.find(buildSettings)
    val buildDir =
        buildDirMatch?.groupValues?.get(1)
            ?: throw GradleException("BUILD_DIR not found in xcodebuild output")
    val derivedDataPath = buildDir.replace("/Build/Products", "")
    return derivedDataPath
}

/**
 * Searches for a xcodeproj starting from the root directory.
 * This function will only work for monorepos and if it is not,
 * the user needs to provide the custom path through the [LinkerExtension] configuration.
 */
internal fun findXcodeprojFile(dir: File): File? {
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
 * Transforms a Kotlin Multiplatform target name to the architecture name that is found inside
 * Sentry's framework directory.
 */
internal fun KotlinNativeTarget.toSentryFrameworkArchitecture(): String? {
    return when (name) {
        "iosSimulatorArm64" -> "ios-arm64_x86_64-simulator"
        "iosX64" -> "ios-arm64_x86_64-simulator"
        "iosArm64" -> "ios-arm64"
        // todo: add more targets
        else -> null
    }
}
