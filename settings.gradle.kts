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
        maven("https://androidx.dev/storage/compose-compiler/repository/")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    }
}

rootProject.name = "sentry-kotlin-multiplatform-sdk"

include(":sentry-kotlin-multiplatform")
includeBuild("sentry-kotlin-multiplatform-gradle-plugin")

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
