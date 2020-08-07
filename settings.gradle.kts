rootProject.name = "sentry-kotlin-multiplatform"

pluginManagement {
    val kotlin_version: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        jcenter()
        maven {
            url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
        }
    }
    // We need this to be able to resolve plugin "com.android.library"
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
                useModule("com.android.tools.build:gradle:3.5.2")
            }
        }
    }
}