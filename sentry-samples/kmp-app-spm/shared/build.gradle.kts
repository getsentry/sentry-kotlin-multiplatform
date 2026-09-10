import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("io.sentry.kotlin.multiplatform.gradle")
    id(Config.spmForKmp)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget()
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
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
    namespace = "sample.kmp.app"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    compileSdk = Config.Android.compileSdkVersion
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Config.Android.minSdkVersion
    }
}

// We depend on project(":sentry-kotlin-multiplatform") directly, so the commonMain auto-install
// (which would add the published SDK dependency) is disabled. The spm4Kmp auto-install stays enabled
// to exercise it: applying the spm4Kmp plugin makes the Sentry KMP plugin add the matching
// Sentry Cocoa Swift package to the Apple targets automatically.
sentryKmp {
    autoInstall.commonMain.enabled = false
}
