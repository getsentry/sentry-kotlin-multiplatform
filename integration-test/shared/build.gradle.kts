plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")

        pod("Sentry") {
            version = Config.Libs.sentryCocoaVersion
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        framework {
            baseName = "shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            val sentryVersion = properties["versionName"].toString()
            println("Loading Sentry KMP version: $sentryVersion")
            implementation("io.sentry:sentry-kotlin-multiplatform:$sentryVersion")
        }
    }
}

android {
    namespace = "sample.kmp.app"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}