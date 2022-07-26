pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    
}
rootProject.name = "sentry-kotlin-multiplatform"

include(":sentry-kotlin-multiplatform")
include("sentry-samples:kmp-app:shared")
include("sentry-samples:kmp-app:androidApp")