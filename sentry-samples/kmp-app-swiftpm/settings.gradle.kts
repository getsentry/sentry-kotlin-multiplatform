pluginManagement {
    // Newer compilers can eliminate the invalid thread-inspector reference from #587.
    val kotlinVersion = if (providers.gradleProperty("nativeCacheRegression").isPresent) "2.3.20" else "2.4.0"
    plugins { kotlin("multiplatform") version kotlinVersion }
    includeBuild("../../sentry-kotlin-multiplatform-gradle-plugin")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "sentry-official-swiftpm-sample"
