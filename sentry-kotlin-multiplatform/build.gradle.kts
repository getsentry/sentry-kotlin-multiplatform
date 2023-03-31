import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin(Config.multiplatform)
    kotlin(Config.cocoapods)
    id(Config.androidGradle)
    id(Config.BuildPlugins.buildConfig)
    `maven-publish`
}

android {
    compileSdk = Config.Android.compileSdkVersion
    defaultConfig {
        minSdk = Config.Android.minSdkVersion
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

kotlin {
    explicitApi()

    android {
        publishLibraryVariants("release")
    }
    jvm()
    ios()
    iosSimulatorArm64()
    watchos()
    watchosSimulatorArm64()
    tvos()
    tvosSimulatorArm64()
    macosX64()
    macosArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation(Config.Libs.kotlinStd)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Config.TestLibs.kotlinCommon)
                implementation(Config.TestLibs.kotlinCommonAnnotation)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Config.Libs.sentryAndroid)
            }
        }
        val androidUnitTest by getting
        val jvmMain by getting
        val jvmTest by getting

        val commonJvmMain by creating {
            dependsOn(commonMain)
            jvmMain.dependsOn(this)
            androidMain.dependsOn(this)
            dependencies {
                implementation(Config.Libs.sentryJava)
            }
        }
        val commonJvmTest by creating {
            dependsOn(commonTest)
            jvmTest.dependsOn(this)
            androidUnitTest.dependsOn(this)
            dependencies {
                implementation(Config.TestLibs.kotlinJunit)
            }
        }

        cocoapods {
            summary = "Official Sentry SDK Kotlin Multiplatform"
            homepage = "https://github.com/getsentry/sentry-kotlin-multiplatform"
            version = "0.0.1"

            pod(Config.Libs.sentryCocoa, "~> ${Config.Libs.sentryCocoaVersion}")

            ios.deploymentTarget = Config.Cocoa.iosDeploymentTarget
            osx.deploymentTarget = Config.Cocoa.osxDeploymentTarget
            tvos.deploymentTarget = Config.Cocoa.tvosDeploymentTarget
            watchos.deploymentTarget = Config.Cocoa.watchosDeploymentTarget
        }

        val iosMain by getting
        val iosSimulatorArm64Main by getting
        val iosTest by getting
        val iosSimulatorArm64Test by getting

        val commonIosMain by creating {
            iosMain.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val commonIosTest by creating {
            iosTest.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }

        val tvosMain by getting
        val tvosSimulatorArm64Main by getting
        val tvosTest by getting
        val tvosSimulatorArm64Test by getting

        val watchosMain by getting
        val watchosSimulatorArm64Main by getting
        val watchosTest by getting
        val watchosSimulatorArm64Test by getting

        val macosX64Main by getting
        val macosArm64Main by getting
        val macosX64Test by getting
        val macosArm64Test by getting

        val commonTvWatchMacOsMain by creating {
            tvosMain.dependsOn(this)
            tvosSimulatorArm64Main.dependsOn(this)
            watchosMain.dependsOn(this)
            watchosSimulatorArm64Main.dependsOn(this)
            macosArm64Main.dependsOn(this)
            macosX64Main.dependsOn(this)
        }
        val commonTvWatchMacOsTest by creating {
            tvosTest.dependsOn(this)
            tvosSimulatorArm64Test.dependsOn(this)
            watchosTest.dependsOn(this)
            watchosSimulatorArm64Test.dependsOn(this)
            macosX64Test.dependsOn(this)
            macosArm64Test.dependsOn(this)
        }

        val commonAppleMain by creating {
            dependsOn(commonMain)
            commonIosMain.dependsOn(this)
            commonTvWatchMacOsMain.dependsOn(this)
        }
        val commonAppleTest by creating {
            dependsOn(commonTest)
            commonIosTest.dependsOn(this)
            commonTvWatchMacOsTest.dependsOn(this)
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
            cinterops.create("Sentry.PrivateSentrySDKOnly") {
                includeDirs("$projectDir/src/nativeInterop/cinterop/SentryPrivateSentrySDKOnly")
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

buildkonfig {
    packageName = "io.sentry.kotlin.multiplatform"
    defaultConfigs {
        buildConfigField(STRING, "SENTRY_KMP_COCOA_SDK_NAME", Config.Sentry.kmpCocoaSdkName)
        buildConfigField(STRING, "SENTRY_KMP_JAVA_SDK_NAME", Config.Sentry.kmpJavaSdkName)
        buildConfigField(STRING, "SENTRY_KMP_ANDROID_SDK_NAME", Config.Sentry.kmpAndroidSdkName)

        buildConfigField(STRING, "VERSION_NAME", project.version.toString())
        buildConfigField(STRING, "SENTRY_JAVA_PACKAGE_NAME", Config.Sentry.javaPackageName)
        buildConfigField(STRING, "SENTRY_ANDROID_PACKAGE_NAME", Config.Sentry.androidPackageName)
        buildConfigField(STRING, "SENTRY_COCOA_PACKAGE_NAME", Config.Sentry.cocoaPackageName)

        buildConfigField(STRING, "SENTRY_JAVA_VERSION", Config.Libs.sentryJavaVersion)
        buildConfigField(STRING, "SENTRY_ANDROID_VERSION", Config.Libs.sentryJavaVersion)
        buildConfigField(STRING, "SENTRY_COCOA_VERSION", Config.Libs.sentryCocoaVersion)
    }
}
