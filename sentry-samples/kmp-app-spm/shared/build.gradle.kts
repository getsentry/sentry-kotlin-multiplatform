import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("io.sentry.kotlin.multiplatform.gradle")
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
    compileSdk = Config.Android.compileSdkVersion
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Config.Android.minSdkVersion
    }
}

// disabling autoInstall because we are using project(":sentry-kotlin-multiplatform") directly
// for our sample apps
sentryKmp {
    autoInstall.commonMain.enabled = false
}
