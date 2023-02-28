pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        kotlin("native.cocoapods").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }
}

rootProject.name = "sentry-kotlin-multiplatform-sdk"

include(":sentry-kotlin-multiplatform")

/*
KMP App with targets:
    - Android
    - iOS with SwiftUI
    - JVM Desktop with Jetpack Compose
 */
include("sentry-samples:kmp-app:shared")
include("sentry-samples:kmp-app:androidApp")
include("sentry-samples:kmp-app:desktopApp")
