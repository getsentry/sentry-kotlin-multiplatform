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
