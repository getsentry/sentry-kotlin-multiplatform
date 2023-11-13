plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
            export(project(":sentry-kotlin-multiplatform"))
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":sentry-kotlin-multiplatform"))
            implementation("io.insert-koin:koin-core:3.4.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
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

// Workaround for KotlinMetadata tasks failing when using ./gradlew build.
// However, running this sample on iOS and Android simulators remains unaffected.
afterEvaluate {
    afterEvaluate {
        tasks.configureEach {
            if (
                name.startsWith("compile") &&
                name.endsWith("KotlinMetadata")
            ) {
                enabled = false
            }
        }
    }
}
