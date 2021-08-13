val kotlin_version: String by extra

plugins {
    kotlin("multiplatform") version "1.5.30-RC"
    kotlin("native.cocoapods") version "1.5.30-RC"
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
    js(IR) {
        browser()
        nodejs()
        compilations.all {
            kotlinOptions {
                moduleKind = "umd"
                sourceMap = true
            }
        }
    }
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
                implementation("io.sentry:sentry-android:5.0.1")
                implementation("androidx.startup:startup-runtime:1.1.0")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
                implementation("junit:junit:4.13.1")
                implementation("androidx.test:core:1.4.0")
                implementation("androidx.test.ext:junit:1.1.3")
                implementation("org.robolectric:robolectric:4.5.1")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.sentry:sentry:5.0.1")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("@sentry/browser", "6.11.0"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-js")
            }
        }

        val appleMain by creating { dependsOn(commonMain) }
        val appleTest by creating { dependsOn(commonTest) }
        val iosMain by getting { dependsOn(appleMain) }
        val tvosMain by getting { dependsOn(appleMain) }
        val watchosMain by getting { dependsOn(appleMain) }
        val macosX64Main by getting { dependsOn(appleMain) }
        val iosTest by getting { dependsOn(appleTest) }
        val tvosTest by getting { dependsOn(appleTest) }
        val watchosTest by getting { dependsOn(appleTest) }
        val macosX64Test by getting { dependsOn(appleTest) }

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

    // workaround for https://youtrack.jetbrains.com/issue/KT-41709 due to having "Meta" in the class name
    // if we need to use this class, we'd need to find a better way to work it out
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
        compilations["main"].cinterops["Sentry"].extraOpts("-compiler-option", "-DSentryMechanismMeta=SentryMechanismMetaUnavailable")
    }
}
