pluginManagement {
    plugins { kotlin("multiplatform") version "2.4.0" }
    includeBuild("../../sentry-kotlin-multiplatform-gradle-plugin")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "sentry-official-swiftpm-sample"
