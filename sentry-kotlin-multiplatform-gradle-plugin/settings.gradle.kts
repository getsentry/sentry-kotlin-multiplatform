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
        // Hosts the spm4Kmp plugin marker + implementation (compiled against for spm4Kmp auto-install)
        gradlePluginPortal()
    }
}

rootProject.name = "sentry-kotlin-multiplatform-gradle-plugin"
