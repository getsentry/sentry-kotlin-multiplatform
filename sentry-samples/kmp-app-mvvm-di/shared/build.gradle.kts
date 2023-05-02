plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
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
        val commonMain by getting {
            dependencies {
                api(project(":sentry-kotlin-multiplatform"))
                implementation("io.insert-koin:koin-core:3.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
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
