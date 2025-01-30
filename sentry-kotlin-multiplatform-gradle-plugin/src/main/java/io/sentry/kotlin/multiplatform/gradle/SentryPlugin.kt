package io.sentry.kotlin.multiplatform.gradle

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
import org.jetbrains.kotlin.konan.target.HostManager
import org.slf4j.LoggerFactory
import java.io.File

internal const val SENTRY_EXTENSION_NAME = "sentryKmp"
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
                    project
                )
            project.extensions.add(LINKER_EXTENSION_NAME, sentryExtension.linker)
            project.extensions.add(AUTO_INSTALL_EXTENSION_NAME, sentryExtension.autoInstall)
            project.extensions.add(
                COCOAPODS_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.cocoapods
            )
            project.extensions.add(
                COMMON_MAIN_AUTO_INSTALL_EXTENSION_NAME,
                sentryExtension.autoInstall.commonMain
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

                // If the user is not using the cocoapods plugin, linking to the framework is not
                // automatic so we have to configure it in the plugin.
                if (!hasCocoapodsPlugin) {
                    configureLinkingOptions(sentryExtension.linker)
                }
            }
        }

    companion object {
        internal val logger by lazy {
            LoggerFactory.getLogger(SentryPlugin::class.java)
        }
    }
}

internal fun Project.installSentryForKmp(
    commonMainAutoInstallExtension: SourceSetAutoInstallExtension
) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension) {
        logger.info("Kotlin Multiplatform plugin not found. Skipping Sentry installation.")
        return
    }

    val unsupportedTargets = listOf("wasm", "js", "mingw", "linux", "androidNative")
    kmpExtension.targets.forEach { target ->
        if (unsupportedTargets.any { unsupported -> target.name.contains(unsupported) }) {
            throw GradleException(
                "Unsupported target: ${target.name}. " +
                    "Cannot auto install in commonMain. " +
                    "Please create an intermediate sourceSet with targets that the Sentry SDK " +
                    "supports (apple, jvm, android) and add the dependency manually."
            )
        }
    }

    val commonMain = kmpExtension.sourceSets.find { it.name.contains("common") }

    val sentryVersion = commonMainAutoInstallExtension.sentryKmpVersion.get()
    commonMain?.dependencies { api("io.sentry:sentry-kotlin-multiplatform:$sentryVersion") }
}

internal fun Project.installSentryForCocoapods(
    cocoapodsAutoInstallExtension: CocoapodsAutoInstallExtension
) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension || kmpExtension.targets.isEmpty() || !HostManager.hostIsMac) {
        logger.info("Skipping Cocoapods installation.")
        return
    }

    (kmpExtension as ExtensionAware).extensions.configure(CocoapodsExtension::class.java) { cocoapods
        ->
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

@Suppress("CyclomaticComplexMethod")
internal fun Project.configureLinkingOptions(linkerExtension: LinkerExtension) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension || kmpExtension.targets.isEmpty() || !HostManager.hostIsMac) {
        logger.info("Skipping linker configuration.")
        return
    }

    var derivedDataPath = ""
    val frameworkPath = linkerExtension.frameworkPath.orNull
    if (frameworkPath == null) {
        val customXcodeprojPath = linkerExtension.xcodeprojPath.orNull
        derivedDataPath = findDerivedDataPath(customXcodeprojPath)
    }

    kmpExtension.appleTargets().all { target ->
        // Contains a set of names where one should match the arch name in the framework
        // This is needed for backwards compatibility as the arch names have changed throughout different versions of the Cocoa SDK
        val frameworkArchitectures = target.toSentryFrameworkArchitecture()
        if (frameworkArchitectures.isEmpty()) {
            logger.warn("Skipping target ${target.name} - unsupported architecture.")
            return@all
        }

        var dynamicFrameworkPath: String? = null
        var staticFrameworkPath: String? = null

        if (frameworkPath?.isNotEmpty() == true) {
            dynamicFrameworkPath = frameworkPath
            staticFrameworkPath = frameworkPath
        } else {
            frameworkArchitectures.forEach {
                val dynamicPath =
                    "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry-Dynamic/Sentry-Dynamic.xcframework/$it"
                logger.info("Looking for dynamic framework at $dynamicPath")
                if (File(dynamicPath).exists()) {
                    logger.info("Found dynamic framework at $dynamicPath")
                    dynamicFrameworkPath = dynamicPath
                    return@forEach
                }

                val staticPath = "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry/Sentry.xcframework/$it"
                logger.info("Looking for dynamic framework at $dynamicPath")
                if (File(staticPath).exists()) {
                    logger.info("Found static framework at $staticPath")
                    staticFrameworkPath = staticPath
                    return@forEach
                }
            }
        }

        if (staticFrameworkPath == null && dynamicFrameworkPath == null) {
            throw GradleException(
                "Sentry Cocoa Framework not found. Make sure the Sentry Cocoa SDK is installed with SPM in your Xcode project."
            )
        }

        target.binaries.all binaries@{ binary ->
            if (binary is TestExecutable) {
                // both dynamic and static frameworks will work for tests
                val path = (dynamicFrameworkPath ?: staticFrameworkPath)!!
                binary.linkerOpts("-rpath", path, "-F$path")
            }

            if (binary is Framework) {
                val finalFrameworkPath = when {
                    binary.isStatic && staticFrameworkPath != null -> staticFrameworkPath
                    !binary.isStatic && dynamicFrameworkPath != null -> dynamicFrameworkPath
                    else -> {
                        logger.warn("Linking to framework failed, no sentry framework found for target ${target.name}")
                        return@binaries
                    }
                }
                binary.linkerOpts("-F$finalFrameworkPath")
                logger.info("Linked framework from $finalFrameworkPath")
            }
        }
    }
}

/**
 * Transforms a Kotlin Multiplatform target name to possible architecture names found inside
 * Sentry's framework directory.
 *
 * Returns a set of possible architecture names because Sentry Cocoa SDK has changed folder naming
 * across different versions. For example:
 * - iosArm64 -> ["ios-arm64", "ios-arm64_arm64e"]
 * - macosArm64 -> ["macos-arm64_x86_64", "macos-arm64_arm64e_x86_64"]
 * *
 * @return Set of possible architecture folder names for the given target. Returns empty set if target is not supported.
 */
internal fun KotlinNativeTarget.toSentryFrameworkArchitecture(): Set<String> = buildSet {
    when (name) {
        "iosSimulatorArm64", "iosX64" -> add("ios-arm64_x86_64-simulator")
        "iosArm64" -> {
            add("ios-arm64")
            add("ios-arm64_arm64e")
        }
        "macosArm64", "macosX64" -> {
            add("macos-arm64_x86_64")
            add("macos-arm64_arm64e_x86_64")
        }
        "tvosSimulatorArm64", "tvosX64" -> {
            add("tvos-arm64_x86_64-simulator")
            add("tvos-arm64_x86_64-simulator")
        }
        "tvosArm64" -> {
            add("tvos-arm64")
            add("tvos-arm64_arm64e")
        }
        "watchosArm32", "watchosArm64" -> {
            add("watchos-arm64_arm64_32_armv7k")
            add("watchos-arm64_arm64_32_arm64e_armv7k")
        }
        "watchosSimulatorArm64", "watchosX64" -> {
            add("watchos-arm64_i386_x86_64-simulator")
            add("watchos-arm64_i386_x86_64-simulator")
        }
        else -> emptySet<String>()
    }
}

private fun Project.findDerivedDataPath(customXcodeprojPath: String? = null): String {
    val xcodeprojPath = customXcodeprojPath ?: findXcodeprojFile(rootDir)?.absolutePath
        ?: throw GradleException("Xcode project file not found")

    return providers.of(DerivedDataPathValueSource::class.java) {
        it.parameters.xcodeprojPath.set(xcodeprojPath)
    }.get()
}

/**
 * Searches for a xcodeproj starting from the root directory. This function will only work for
 * monorepos and if it is not, the user needs to provide the custom path through the
 * [LinkerExtension] configuration.
 */
internal fun findXcodeprojFile(dir: File): File? {
    val ignoredDirectories = listOf("build", "DerivedData")

    fun searchDirectory(directory: File): File? {
        val files = directory.listFiles() ?: return null

        return files.firstNotNullOfOrNull { file ->
            when {
                file.name in ignoredDirectories -> null
                file.extension == "xcodeproj" -> file
                file.isDirectory -> searchDirectory(file)
                else -> null
            }
        }
    }

    return searchDirectory(dir)
}

internal fun KotlinMultiplatformExtension.appleTargets() =
    targets.withType(KotlinNativeTarget::class.java).matching {
        it.konanTarget.family.isAppleFamily
    }
