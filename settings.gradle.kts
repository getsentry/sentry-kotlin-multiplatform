pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version("1.7.10")
        id("org.jetbrains.compose").version("1.2.0-alpha01-dev753")
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
