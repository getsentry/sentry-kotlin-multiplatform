import io.sentry.kotlin.multiplatform.gradle.AppleDependencyProvider

plugins {
    kotlin("multiplatform") version "2.4.0"
    id("io.sentry.kotlin.multiplatform.gradle")
}

repositories {
    // Use this checkout's SDK together with its plugin/native version pin.
    exclusiveContent {
        forRepository { maven { url = uri("../../sentry-kotlin-multiplatform/build/sentry-local-publish") } }
        filter { includeModuleByRegex("io.sentry", "sentry-kotlin-multiplatform.*") }
    }
    mavenCentral()
}

sentryKmp {
    // Select explicitly because this minimal sample has no other SwiftPM dependencies.
    autoInstall.apple.provider.set(AppleDependencyProvider.SWIFT_PM)
}

kotlin {
    iosArm64()
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "SentrySwiftPmSample"
            isStatic = true
        }
    }
    macosArm64()
    tvosSimulatorArm64()
    watchosSimulatorArm64()
    sourceSets.commonTest.dependencies { implementation(kotlin("test")) }
}
