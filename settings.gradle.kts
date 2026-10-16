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
        exclusiveContent {
            forRepository {
                ivy {
                    name = "sentryCocoaReleases"
                    url = uri("https://github.com/getsentry/sentry-cocoa/releases/download")
                    patternLayout { artifact("[revision]/[artifact].[ext]") }
                    metadataSources { artifact() }
                }
            }
            filter { includeModule("io.sentry.native", "Sentry-Dynamic") }
        }
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
