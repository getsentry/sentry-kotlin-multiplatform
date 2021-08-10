rootProject.name = "sentry-kotlin-multiplatform"

pluginManagement {
    val kotlin_version: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    // We need this to be able to resolve plugin "com.android.library"
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:4.2.0")
            }
        }
    }
}