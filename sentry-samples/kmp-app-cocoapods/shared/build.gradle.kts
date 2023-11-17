plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")

        pod("Sentry", "8.15.2")

        framework {
            baseName = "shared"
            export(project(":sentry-kotlin-multiplatform"))
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":sentry-kotlin-multiplatform"))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 27
        targetSdk = 32
    }
}
