import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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

    // ios shortcut targets: iosArm64, iosX64
    // iosSimulatorArm64 targets: iOS simulator on Apple Silicon
    ios()
    iosSimulatorArm64()

    // watchos shortcut targets: watchosArm32, watchosArm64, watchosX64
    // watchosSimulatorArm64 targets: watchOS simulator on Apple Silicon
    watchos()
    watchosSimulatorArm64()

    // tvos shortcut targets: tvosArm64, tvosX64
    // tvosSimulatorArm64 targets: tvOS simulator on Apple Silicon
    tvos()
    tvosSimulatorArm64()

    macosX64()
    macosArm64()

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

        // Common Apple sourceset - contains all apple targets
        val commonAppleMain by creating { dependsOn(commonMain) }
        val commonAppleTest by creating { dependsOn(commonTest) }

        // Common iOS sourceset - contains only iOS targets and depends on appleMain
        // This is needed since Sentry also has iOS only features such as screenshots
        val commonIosMain by creating { dependsOn(commonAppleMain) }
        val commonIosTest by creating { dependsOn(commonAppleTest) }
        val iosMain by getting { dependsOn(commonIosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(commonIosMain) }
        val iosTest by getting { dependsOn(commonIosTest) }
        val iosSimulatorArm64Test by getting { dependsOn(commonIosTest) }

        val commonTvWatchMacOsMain by creating { dependsOn(commonAppleMain) }
        val commonTvWatchMacOsTest by creating { dependsOn(commonAppleTest) }

        val tvosMain by getting { dependsOn(commonTvWatchMacOsMain) }
        val tvosSimulatorArm64Main by getting { dependsOn(commonTvWatchMacOsMain) }
        val tvosTest by getting { dependsOn(commonTvWatchMacOsTest) }

        val watchosMain by getting { dependsOn(commonTvWatchMacOsMain) }
        val watchosSimulatorArm64Main by getting { dependsOn(commonTvWatchMacOsMain) }
        val watchosTest by getting { dependsOn(commonTvWatchMacOsTest) }

        val macosX64Main by getting { dependsOn(commonTvWatchMacOsMain) }
        val macosArm64Main by getting { dependsOn(commonTvWatchMacOsMain) }
        val macosX64Test by getting { dependsOn(commonTvWatchMacOsTest) }

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
        watchosSimulatorArm64(),
        tvosArm64(),
        tvosX64(),
        tvosSimulatorArm64(),
        macosX64(),
        macosArm64()
    ).forEach {
        it.compilations.getByName("main") {
            cinterops.create("Sentry.NSException") {
                includeDirs("$projectDir/src/nativeInterop/cinterop/SentryNSException")
            }
            cinterops.create("Sentry.Scope") {
                includeDirs("$projectDir/src/nativeInterop/cinterop/SentryScope")
            }
        }
    }

    // workaround for https://youtrack.jetbrains.com/issue/KT-41709 due to having "Meta" in the class name
    // if we need to use this class, we'd need to find a better way to work it out
    targets.withType<KotlinNativeTarget>().all {
        compilations["main"].cinterops["Sentry"].extraOpts(
            "-compiler-option",
            "-DSentryMechanismMeta=SentryMechanismMetaUnavailable"
        )
    }
}
