plugins {
    id("com.android.application")
    kotlin("android")
    id("io.sentry.android.gradle") version "3.5.0"
}

android {
    namespace = "sentry.kmp.demo.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "sentry.kmp.demo"
        minSdk = 21
        targetSdk = 34
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
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
}

dependencies {
    implementation(rootProject.project(":sentry-samples:kmp-app-mvvm-di:shared"))
    implementation(Config.Libs.Samples.koinAndroid)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.lifecycle:lifecycle-runtime:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.navigation:navigation-runtime:2.7.5")
    implementation("androidx.compose.compiler:compiler:1.5.5")
    implementation("androidx.compose.ui:ui:1.6.0-beta02")
    implementation("androidx.compose.ui:ui-tooling:1.6.0-beta02")
    implementation("androidx.compose.foundation:foundation:1.6.0-beta02")
    implementation("androidx.compose.material:material:1.6.0-beta02")
}

// Prevent Sentry from being included in the Android app through the AGP.
configurations {
    compileOnly {
        exclude(group = "io.sentry", module = "sentry")
        exclude(group = "io.sentry", module = "sentry-android")
    }
}

sentry {
    autoUploadProguardMapping.set(false)
}
