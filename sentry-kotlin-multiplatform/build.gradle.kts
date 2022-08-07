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
    watchos()
    tvos()
    macosX64()

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

        val commonJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.sentry:sentry:6.1.4")
            }
        }
        val commonJvmTest by creating {
            dependsOn(commonTest)
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
            }
        }

        val jvmMain by getting { dependsOn(commonJvmMain) }
        val jvmTest by getting { dependsOn(commonJvmTest) }

        val androidMain by getting {
            dependsOn(commonJvmMain)
            dependencies {
                implementation("io.sentry:sentry-android:6.1.4") {
                    // avoid duplicate dependencies since we depend on commonJvmMain
                    exclude("io.sentry", "sentry")
                }
            }
        }
        val androidTest by getting { dependsOn(commonJvmTest) }

        val appleMain by creating { dependsOn(commonMain) }
        val iosMain by getting { dependsOn(appleMain) }
        val iosSimulatorArm64Main by getting { dependsOn(appleMain) }
        val appleTest by creating { dependsOn(commonTest) }
        val iosTest by getting { dependsOn(appleTest) }
        val iosSimulatorArm64Test by getting { dependsOn(appleTest) }
        val tvosMain by getting { dependsOn(appleMain) }
        val tvosTest by getting { dependsOn(appleTest) }
        val watchosMain by getting { dependsOn(appleMain) }
        val watchosTest by getting { dependsOn(appleTest) }
        val macosX64Main by getting { dependsOn(appleMain) }
        val macosX64Test by getting { dependsOn(appleTest) }

        cocoapods {
            summary = "Official Sentry SDK Kotlin Multiplatform"
            homepage = "https://github.com/getsentry/sentry-kotlin-multiplatform"

            pod("Sentry", "~> 7.21.0")

            ios.deploymentTarget = "9.0"
            osx.deploymentTarget = "10.10"
            tvos.deploymentTarget = "9.0"
            watchos.deploymentTarget = "2.0"
        }
    }

    listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64(),
        watchosArm32(),
        watchosArm64(),
        watchosX64(),
        tvosArm64(),
        tvosX64(),
        macosX64()
    ).forEach {
        it.compilations.getByName("main") {
            cinterops.create("Sentry.NSException") {
                includeDirs("$projectDir/src/nativeInterop/cinterop/Sentry")
            }
        }
    }

    // workaround for https://youtrack.jetbrains.com/issue/KT-41709 due to having "Meta" in the class name
    // if we need to use this class, we'd need to find a better way to work it out
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
        compilations["main"].cinterops["Sentry"].extraOpts(
            "-compiler-option",
            "-DSentryMechanismMeta=SentryMechanismMetaUnavailable"
        )
    }
}

