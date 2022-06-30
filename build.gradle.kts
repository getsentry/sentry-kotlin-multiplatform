val kotlin_version: String by extra

plugins {
    kotlin("multiplatform") version "1.7.0"
    id("com.android.library")
    `maven-publish`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
}

group = "io.sentry.kotlin.multiplatform"
version = "0.0.1"

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(14)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "0.0.1"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    // linking the manifest file manually due to having it in the "androidMain" source set
    sourceSets.getByName("main").manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

kotlin {
    android {
        publishAllLibraryVariants()
    }

    jvm()
    ios()
    watchos()
    tvos()
    macosX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.sentry:sentry-android:6.1.4")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.sentry:sentry:6.1.4")
            }
        }

        val appleMain by creating { dependsOn(commonMain) }
        val iosMain by getting { dependsOn(appleMain) }
        val tvosMain by getting { dependsOn(appleMain) }
        val watchosMain by getting { dependsOn(appleMain) }
        val macosX64Main by getting { dependsOn(appleMain) }

    }

}
