plugins {
    id("com.android.application")
    kotlin("android")
    id("io.sentry.android.gradle") version "3.5.0"
}

android {
    namespace = "sentry.kmp.demo.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "sentry.kmp.demo"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    signingConfigs {
        create("release") {
            storeFile = file("sentry.keystore")
            storePassword = "sentry"
            keyAlias = "Sentry Android Key"
            keyPassword = "sentry"
        }
    }
    buildTypes {
        getByName("release") {
            isDefault = true
            isMinifyEnabled = true
            proguardFiles.add(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("release")
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
        kotlinCompilerExtensionVersion = "1.5.4"
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
    implementation("io.insert-koin:koin-android:3.4.0")
    implementation("androidx.compose.compiler:compiler:1.5.4")
    implementation("androidx.compose.ui:ui:1.5.0-alpha02")
    implementation("androidx.compose.ui:ui-tooling:1.5.0-alpha02")
    implementation("androidx.compose.foundation:foundation:1.5.0-alpha02")
    implementation("androidx.compose.material:material:1.5.0-alpha02")
}

// Prevent Sentry from being included in the Android app through the AGP.
configurations {
    compileOnly {
        exclude(group = "io.sentry", module = "sentry")
        exclude(group = "io.sentry", module = "sentry-android")
    }
}

sentry {
    autoUploadProguardMapping.set(true)
}
