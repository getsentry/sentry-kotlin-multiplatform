plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

dependencies {
    implementation(rootProject.project(":sentry-samples:compose-multiplatform-app:common"))
    implementation("androidx.activity:activity-compose:1.5.1")
}

android {
    compileSdkVersion(31)
    defaultConfig {
        applicationId = "com.example.android"
        minSdkVersion(24)
        targetSdkVersion(31)
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
