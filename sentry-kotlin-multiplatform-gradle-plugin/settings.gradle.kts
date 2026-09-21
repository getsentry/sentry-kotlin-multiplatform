pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // Resolves the spm4Kmp implementation.
        gradlePluginPortal()
    }
}

rootProject.name = "sentry-kotlin-multiplatform-gradle-plugin"
