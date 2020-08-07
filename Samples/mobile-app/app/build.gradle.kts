plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.application")
    id("kotlin-android-extensions")
    id("org.jetbrains.kotlin.native.cocoapods")
}

// Used for cocoapods
version = "0.0.1"

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "io.sentry.kotlin.mpp_app_android"
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.2.0-rc02")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-rc1")
}

kotlin {
    android("android")
    // This is for iPhone emulator
    // Switch here to iosArm64 (or iosArm32) to build library for iPhone device
    iosX64("ios")
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
                implementation("io.sentry.kotlin.multiplatform:sentry-kotlin-multiplatform:0.0.1")
            }
        }
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }
    }

    cocoapods {
        summary = "Sample Mobile App"
        homepage = "We do not have one"

        ios.deploymentTarget = "8.0"
        osx.deploymentTarget = "10.10"
        tvos.deploymentTarget = "9.0"
        watchos.deploymentTarget = "2.0"
    }
}
