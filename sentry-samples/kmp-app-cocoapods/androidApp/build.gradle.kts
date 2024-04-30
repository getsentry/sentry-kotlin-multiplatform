import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    id("io.sentry.android.gradle") version "4.0.0"
}

android {
    compileSdk = Config.Android.compileSdkVersion
    defaultConfig {
        applicationId = "sample.kmp.app.android"
        minSdk = Config.Android.minSdkVersion
        targetSdk = Config.Android.targetSdkVersion
        versionCode = 1
        versionName = "1.0"
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
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(rootProject.project(":sentry-samples:kmp-app-cocoapods:shared"))
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
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
