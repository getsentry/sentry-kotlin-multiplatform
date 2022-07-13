buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
        classpath("com.android.tools.build:gradle:7.2.1")
    }
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        google()
    }
    group = "io.sentry.kotlin.multiplatform"
    version = properties["versionName"].toString()
}