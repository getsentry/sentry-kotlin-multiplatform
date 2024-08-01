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
                // automatic so we have to take care of that
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
    if (kmpExtension !is KotlinMultiplatformExtension) {
        logger.info("Kotlin Multiplatform plugin not found. Skipping Cocoapods installation.")
        return
    }

    if (kmpExtension.appleTargets().isEmpty()) {
        logger.info("No Apple targets found. Skipping Cocoapods installation.")
        return
    }

    if (!HostManager.hostIsMac) {
        logger.info("Host is not macOS. Skipping Cocoapods installation.")
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

internal fun Project.configureLinkingOptions(linkerExtension: LinkerExtension) {
    val kmpExtension = extensions.findByName(KOTLIN_EXTENSION_NAME)
    if (kmpExtension !is KotlinMultiplatformExtension) {
        logger.info("Kotlin Multiplatform plugin not found. Skipping linker configuration.")
        return
    }

    if (kmpExtension.appleTargets().isEmpty()) {
        logger.info("No Apple targets found. Skipping linker configuration.")
        return
    }

    if (!HostManager.hostIsMac) {
        logger.info("Host is not macOS. Skipping linker configuration.")
        return
    }

    val customXcodeprojPath = linkerExtension.xcodeprojPath.orNull
    val derivedDataPath = findDerivedDataPath(customXcodeprojPath)

    kmpExtension.appleTargets().all { target ->
        val frameworkArchitecture = target.toSentryFrameworkArchitecture()
        if (frameworkArchitecture == null) {
            logger.warn("Skipping target ${target.name}. Unsupported architecture.")
            return@all
        }

        @Suppress("MaxLineLength")
        val dynamicFrameworkPath =
            "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry-Dynamic/Sentry-Dynamic.xcframework/$frameworkArchitecture"
        val staticFrameworkPath =
            "$derivedDataPath/SourcePackages/artifacts/sentry-cocoa/Sentry/Sentry.xcframework/$frameworkArchitecture"

        val dynamicFrameworkExists = File(dynamicFrameworkPath).exists()
        val staticFrameworkExists = File(staticFrameworkPath).exists()

        if (!dynamicFrameworkExists && !staticFrameworkExists) {
            throw GradleException(
                "Sentry Cocoa Framework not found at $dynamicFrameworkPath or $staticFrameworkPath"
            )
        }

        target.binaries.all binaries@{ binary ->
            if (binary is TestExecutable) {
                // both dynamic and static frameworks will work for tests
                val frameworkPath =
                    if (dynamicFrameworkExists) dynamicFrameworkPath else staticFrameworkPath
                binary.linkerOpts("-rpath", frameworkPath, "-F$frameworkPath")
            }

            if (binary is Framework) {
                val frameworkPath = when {
                    binary.isStatic && staticFrameworkExists -> staticFrameworkPath
                    !binary.isStatic && dynamicFrameworkExists -> dynamicFrameworkPath
                    else -> {
                        logger.warn("Linking to framework failed, no sentry framework found for target ${target.name}")
                        return@binaries
                    }
                }
                binary.linkerOpts("-F$frameworkPath")
                logger.info("Linked framework from $frameworkPath")
            }
        }
    }
}

/**
 * Transforms a Kotlin Multiplatform target name to the architecture name that is found inside
 * Sentry's framework directory.
 */
internal fun KotlinNativeTarget.toSentryFrameworkArchitecture(): String? {
    return when (name) {
        "iosSimulatorArm64", "iosX64" -> "ios-arm64_x86_64-simulator"
        "iosArm64" -> "ios-arm64"
        "macosArm64", "macosX64" -> "macos-arm64_x86_64"
        "tvosSimulatorArm64", "tvosX64" -> "tvos-arm64_x86_64-simulator"
        "tvosArm64" -> "tvos-arm64"
        "watchosArm32", "watchosArm64" -> "watchos-arm64_arm64_32_armv7k"
        "watchosSimulatorArm64", "watchosX64" -> "watchos-arm64_i386_x86_64-simulator"
        else -> null
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

private fun KotlinMultiplatformExtension.appleTargets() =
    targets.withType(KotlinNativeTarget::class.java).matching {
        it.konanTarget.family.isAppleFamily
    }
