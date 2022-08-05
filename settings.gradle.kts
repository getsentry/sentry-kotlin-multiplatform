pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version("1.7.0")
        kotlin("android").version("1.7.0")
        id("com.android.application").version("4.2.2")
        id("com.android.library").version("4.2.2")
        id("org.jetbrains.compose").version("1.2.0-alpha01-dev753")
    }
}

rootProject.name = "sentry-kotlin-multiplatform"

include(":sentry-kotlin-multiplatform")

// KMP Sample App with Android, iOS
include("sentry-samples:kmp-app:shared")
include("sentry-samples:kmp-app:androidApp")
include("sentry-samples:compose-multiplatform-app")

// Compose Multiplatform with Android, Desktop JVM, iOS SwiftUI
include("sentry-samples:compose-multiplatform-app:common")
include("sentry-samples:compose-multiplatform-app:android")
include("sentry-samples:compose-multiplatform-app:desktop")
