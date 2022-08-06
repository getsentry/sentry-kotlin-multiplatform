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
    compileSdk = 32
    defaultConfig {
        applicationId = "com.example.android"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
