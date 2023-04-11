plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "io.sentry.kmp.demo.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "io.sentry.kmp.demo"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0-dev-k1.8.0-33c0ad36f83"
    }
}

dependencies {
    implementation(rootProject.project(":sentry-samples:kmp-app-mvvm-di:shared"))
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.1")
    implementation("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.navigation:navigation-runtime:2.5.3")
    implementation("io.insert-koin:koin-android:3.2.0")
    implementation("io.insert-koin:koin-core:3.2.0")
    implementation("androidx.compose.compiler:compiler:1.4.0-dev-k1.8.0-33c0ad36f83")
    implementation("androidx.compose.ui:ui:1.5.0-alpha02")
    implementation("androidx.compose.ui:ui-tooling:1.5.0-alpha02")
    implementation("androidx.compose.foundation:foundation:1.5.0-alpha02")
    implementation("androidx.compose.material:material:1.5.0-alpha02")
}
