pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "kmp-app-spm"

include(":shared")

includeBuild("../..") {
    dependencySubstitution {
        substitute(module("com.rickclephas.kmp:nsexception-kt-sentry"))
            .using(project(":sentry-kotlin-multiplatform"))
    }
}
