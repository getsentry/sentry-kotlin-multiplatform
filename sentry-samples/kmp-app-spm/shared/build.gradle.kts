import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("io.sentry.kotlin.multiplatform.gradle")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget()
    jvm()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = false
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
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

// disabling autoInstall because we are using project(":sentry-kotlin-multiplatform") directly
// for our sample apps
sentryKmp {
    autoInstall.commonMain.enabled = false
}
