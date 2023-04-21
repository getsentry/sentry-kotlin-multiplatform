pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        mavenLocal()
        google()
    }
}

rootProject.name = "sentry-kotlin-multiplatform-sdk"

include(":sentry-kotlin-multiplatform")

/*
Simple KMP App with targets:
    - Android
    - iOS with SwiftUI and SPM
    - JVM Desktop with Jetpack Compose
 */
include("sentry-samples:kmp-app-spm:shared")
include("sentry-samples:kmp-app-spm:androidApp")
include("sentry-samples:kmp-app-spm:desktopApp")

/*
Simple KMP App with targets:
    - Android
    - iOS with SwiftUI and Cocoapods
    - JVM Desktop with Jetpack Compose
 */
include("sentry-samples:kmp-app-cocoapods:shared")
include("sentry-samples:kmp-app-cocoapods:androidApp")
include("sentry-samples:kmp-app-cocoapods:desktopApp")
