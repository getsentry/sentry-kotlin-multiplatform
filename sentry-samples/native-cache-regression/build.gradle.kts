import io.sentry.kotlin.multiplatform.gradle.AppleDependencyProvider

plugins {
    // Kotlin 2.4 removes the class reference that exposes #587.
    kotlin("multiplatform") version "2.3.20"
    id("io.sentry.kotlin.multiplatform.gradle")
}

repositories {
    exclusiveContent {
        // Dependencies under Gradle user home are automatically cached by Kotlin/Native.
        forRepository { maven { url = uri(File(gradle.gradleUserHomeDir, "sentry-native-cache-regression")) } }
        filter { includeModuleByRegex("io.sentry", "sentry-kotlin-multiplatform.*") }
    }
    mavenCentral()
}

kotlin {
    iosSimulatorArm64()
    sourceSets.commonTest.dependencies { implementation(kotlin("test")) }
}

val cocoaVersion = sentryKmp.autoInstall.apple.sentryCocoaVersion
val cocoa = file("../../sentry-kotlin-multiplatform/build/sentryCocoa/${cocoaVersion.get()}/Sentry-Dynamic.xcframework")
sentryKmp {
    // SwiftPM's local Swift runtime paths would hide the bridge linker failure in #588.
    autoInstall.apple.provider.set(AppleDependencyProvider.NONE)
    linker.frameworkPath.set(cocoa.path)
}

val linkMap = file("${layout.buildDirectory.get()}/native-cache-linkmap.txt")
val binary = kotlin.iosSimulatorArm64().binaries.getTest("DEBUG")
binary.linkerOpts("-map", linkMap.absolutePath)
binary.linkTaskProvider.configure {
    inputs.dir(cocoa)
    outputs.file(linkMap)
    // Relink each run so the cache check inspects this build.
    outputs.upToDateWhen { false }
    doFirst { linkMap.parentFile.mkdirs() }
    doLast {
        check(linkMap.readLines().any { it.startsWith("[") && it.contains("/libio.sentry:sentry-kotlin-multiplatform-cache.a(") }) {
            "Sentry native cache was not linked"
        }
        logger.lifecycle("Verified the Sentry native cache was linked")
    }
}
