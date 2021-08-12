val kotlin_version: String by extra

plugins {
    kotlin("multiplatform") version "1.5.30-RC"
    id("com.android.library")
    id("org.jetbrains.kotlin.native.cocoapods") version "1.5.30-RC"
    `maven-publish`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

group = "io.sentry.kotlin.multiplatform"
version = "0.0.1"

kotlin {
    android {
        publishAllLibraryVariants()
    }

    jvm()
    js {
        browser {
        }
        nodejs {
        }
    }
    ios()

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
                implementation("io.sentry:sentry-android:5.0.1")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.sentry:sentry-core:5.0.1")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-js")
            }
        }

        cocoapods {
            summary = "Official Sentry SDK for iOS / tvOS / macOS / watchOS"
            homepage = "https://github.com/getsentry/sentry-cocoa"

            pod("Sentry", "~> 7.1.4")

            ios.deploymentTarget = "9.0"
            osx.deploymentTarget = "10.10"
            tvos.deploymentTarget = "9.0"
            watchos.deploymentTarget = "2.0"
        }
    }
}

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
}