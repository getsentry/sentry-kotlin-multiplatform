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
    packagingOptions {
        resources.excludes.add("META-INF/*.kotlin_module")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(rootProject.project(":sentry-samples:kmp-app-mvvm-di:shared"))
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("io.insert-koin:koin-android:3.2.0")
    implementation("io.insert-koin:koin-core:3.2.0")
    implementation("junit:junit:4.13.2")
    implementation("com.android.tools:desugar_jdk_libs:1.1.8")
    implementation("androidx.compose.compiler:compiler:1.4.0-dev-k1.8.0-33c0ad36f83")
    implementation("androidx.compose.ui:ui:1.4.0-alpha03")
    implementation("androidx.compose.ui:ui-tooling:1.4.0-alpha03")
    implementation("androidx.compose.foundation:foundation:1.4.0-alpha03")
    implementation("androidx.compose.material:material:1.4.0-alpha03")
}
