plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    `maven-publish`
}

android {
    compileSdk = 30
    defaultConfig {
        minSdk = 16
        targetSdk = 30
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    // linking the manifest file manually due to having it in the "androidMain" source set
    sourceSets.getByName("main").manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

kotlin {
    android {
        publishAllLibraryVariants()
    }

    jvm()
    ios()
    iosSimulatorArm64()

    /*
    watchos()
    tvos()
    macosX64()
     */

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.sentry:sentry-android:6.1.4")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.sentry:sentry:6.1.4")
            }
        }
        val appleMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(project(":sentry-kotlin-multiplatform-nsexceptions"))
            }
        }
        val iosMain by getting { dependsOn(appleMain) }
        val iosSimulatorArm64Main by getting { dependsOn(appleMain) }
        /*
        val tvosMain by getting { dependsOn(appleMain) }
        val watchosMain by getting { dependsOn(appleMain) }
        val macosX64Main by getting { dependsOn(appleMain) }
        */
        cocoapods {
            summary = "Official Sentry SDK for iOS / tvOS / macOS / watchOS"
            homepage = "https://github.com/getsentry/sentry-cocoa"

            pod("Sentry", "~> 7.19.0")

            ios.deploymentTarget = "9.0"
            // osx.deploymentTarget = "10.10"
            // tvos.deploymentTarget = "9.0"
            // watchos.deploymentTarget = "2.0"
        }
    }

    // workaround for https://youtrack.jetbrains.com/issue/KT-41709 due to having "Meta" in the class name
    // if we need to use this class, we'd need to find a better way to work it out
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
        compilations["main"].cinterops["Sentry"].extraOpts("-compiler-option", "-DSentryMechanismMeta=SentryMechanismMetaUnavailable")
    }
}

