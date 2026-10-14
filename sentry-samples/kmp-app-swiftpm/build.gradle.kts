import io.sentry.kotlin.multiplatform.gradle.AppleDependencyProvider
import java.util.Properties
import java.util.zip.ZipFile

plugins {
    kotlin("multiplatform")
    id("io.sentry.kotlin.multiplatform.gradle")
}

val nativeCacheRegression = providers.gradleProperty("nativeCacheRegression").isPresent
val sentryRepository =
    if (nativeCacheRegression) {
        File(gradle.gradleUserHomeDir, "sentry-native-cache-regression")
    } else {
        file("../../sentry-kotlin-multiplatform/build/sentry-local-publish")
    }
if (nativeCacheRegression) layout.buildDirectory.set(layout.projectDirectory.dir("build/native-cache-regression"))

repositories {
    // Use this checkout's SDK together with its plugin/native version pin.
    exclusiveContent {
        forRepository { maven { url = uri(sentryRepository) } }
        filter { includeModuleByRegex("io.sentry", "sentry-kotlin-multiplatform.*") }
    }
    mavenCentral()
}

kotlin {
    iosArm64()
    iosSimulatorArm64 {
        if (nativeCacheRegression) return@iosSimulatorArm64
        binaries.framework {
            baseName = "SentrySwiftPmSample"
            isStatic = true
        }
        binaries.framework("Dynamic") {
            baseName = "SentrySwiftPmSampleDynamic"
            isStatic = false
        }
    }
    macosArm64()
    tvosSimulatorArm64()
    watchosSimulatorArm64()
    sourceSets.commonTest.dependencies { implementation(kotlin("test")) }
}

if (nativeCacheRegression) {
    val cocoaVersion = sentryKmp.autoInstall.apple.sentryCocoaVersion
    val cocoa = file("../../sentry-kotlin-multiplatform/build/sentryCocoa/${cocoaVersion.get()}/Sentry-Dynamic.xcframework")
    // SwiftPM adds local Swift runtime paths that hide the published bridge's linker regression.
    sentryKmp {
        autoInstall.apple.provider.set(AppleDependencyProvider.NONE)
        linker.frameworkPath.set(cocoa.path)
    }
    val linkMap = file("${layout.buildDirectory.get()}/sentry-native-cache-linkmap.txt")
    val binary = kotlin.iosSimulatorArm64().binaries.getTest("DEBUG")
    binary.linkerOpts("-map", linkMap.absolutePath)
    binary.linkTaskProvider.configure {
        inputs.dir(cocoa)
        outputs.file(linkMap)
        // Relink each run so the assertions inspect this build.
        outputs.upToDateWhen { false }
        doFirst { linkMap.parentFile.mkdirs() }
        doLast {
            val objects = linkMap.readLines().filter { it.startsWith("[") }
            val sdkKlibs = libraries.filter { it.name.startsWith("sentry-kotlin-multiplatform") && it.extension == "klib" }
            check(sdkKlibs.files.size == 3) { "Expected the SDK and two Cocoa interop KLIBs" }
            sdkKlibs.forEach { klib ->
                ZipFile(klib).use { archive ->
                    val manifest = Properties()
                    archive.getInputStream(archive.getEntry("default/manifest")).use { manifest.load(it) }
                    check(!Regex("""(^|\s)-[FL]""").containsMatchIn(manifest.getProperty("linkerOpts", ""))) {
                        "Published ${klib.name} contains framework or library search paths"
                    }
                    val name = manifest.getProperty("unique_name")
                    check(objects.any { it.contains("/lib$name-cache.a(") }) {
                        "Sentry native cache was not linked: $name"
                    }
                }
            }
            logger.lifecycle("Verified the SDK and both Cocoa interop native caches were linked")
        }
    }
}
