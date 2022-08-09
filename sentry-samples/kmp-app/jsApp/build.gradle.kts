plugins {
    kotlin("js")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(rootProject.project(":sentry-samples:kmp-app:shared"))
}

kotlin {
    js(IR) {
        binaries.executable()
        browser()
    }
}

rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
    versions.webpackCli.version = "4.10.0"
}
