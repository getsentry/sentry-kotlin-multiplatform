pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
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

/*
KMP App with targets:
    - Android
    - iOS with SwiftUI
    - JVM Desktop with Jetpack Compose
 */
include("sentry-samples:kmp-app:shared")
include("sentry-samples:kmp-app:androidApp")
include("sentry-samples:kmp-app:desktopApp")

/*
KMP App with targets:
    - Android with Jetpack Compose
    - iOS with SwiftUI
    - Dependency Injection with Koin
    - MVVM Architecture
 */
include("sentry-samples:kmp-app-mvvm-di:iosApp")
include("sentry-samples:kmp-app-mvvm-di:androidApp")
include("sentry-samples:kmp-app-mvvm-di:shared")
