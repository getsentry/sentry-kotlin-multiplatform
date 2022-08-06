pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "sentry-kotlin-multiplatform-sdk"

include(":sentry-kotlin-multiplatform")

// KMP Sample App with Android, iOS
include("sentry-samples:kmp-app:shared")
include("sentry-samples:kmp-app:androidApp")
include("sentry-samples:compose-multiplatform-app")

// Compose Multiplatform with Android, Desktop JVM, iOS SwiftUI
include("sentry-samples:compose-multiplatform-app:common")
include("sentry-samples:compose-multiplatform-app:android")
include("sentry-samples:compose-multiplatform-app:desktop")
