import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Config.multiplatform)
    kotlin(Config.cocoapods)
    id(Config.androidGradle)
    id(Config.BuildPlugins.buildConfig)
    kotlin(Config.kotlinSerializationPlugin)
    id(Config.QualityPlugins.kover)
    id(Config.QualityPlugins.binaryCompatibility)
    `maven-publish`
}

koverReport {
    defaults {
        // adds the contents of the reports of `release` Android build variant to default reports
        mergeWith("release")
    }
}

android {
    compileSdk = Config.Android.compileSdkVersion
    defaultConfig {
        minSdk = Config.Android.minSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release")
    }
    jvm()
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    watchosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosX64()
    tvosSimulatorArm64()
    tvosArm64()
    tvosX64()
    macosX64()
    macosArm64()

    sourceSets {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }

        all {
            languageSettings.apply {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlinx.cinterop.UnsafeNumber")
                optIn("kotlin.experimental.ExperimentalNativeApi")
            }
        }

        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
        }

        commonTest.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.coroutines.test)
            implementation(libs.ktor)
            implementation(libs.ktor.serialization)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlin.test.common)
            implementation(libs.kotlin.test.annotations)
        }

        androidMain.dependencies {
            api(libs.sentry.android)
        }

        // androidUnitTest.dependencies doesn't exist
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.roboletric)
                implementation(libs.junit.ktx)
                implementation(libs.mockito.core)
            }
        }

        val commonJvmMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                api(libs.sentry.java)
            }
        }

        androidMain.get().dependsOn(commonJvmMain)
        jvmMain.get().dependsOn(commonJvmMain)

        val commonJvmTest by creating {
            dependsOn(commonTest.get())
            dependencies {
                implementation(libs.kotlin.test.junit)
                implementation(libs.ktor.okhttp)
            }
        }

        androidUnitTest.dependsOn(commonJvmTest)
        jvmTest.get().dependsOn(commonJvmTest)

        appleTest.dependencies {
            implementation(libs.ktor.darwin)
        }

        val commonTvWatchMacOsMain by creating {
            dependsOn(appleMain.get())
        }

        tvosMain.get().dependsOn(commonTvWatchMacOsMain)
        macosMain.get().dependsOn(commonTvWatchMacOsMain)
        watchosMain.get().dependsOn(commonTvWatchMacOsMain)

        val commonTvWatchMacOsTest by creating {
            dependsOn(appleTest.get())
        }

        tvosTest.get().dependsOn(commonTvWatchMacOsTest)
        macosTest.get().dependsOn(commonTvWatchMacOsTest)
        watchosTest.get().dependsOn(commonTvWatchMacOsTest)

        cocoapods {
            summary = "Official Sentry SDK Kotlin Multiplatform"
            homepage = "https://github.com/getsentry/sentry-kotlin-multiplatform"
            version = "0.0.1"

            pod(Config.Libs.sentryCocoa) {
                version = Config.Libs.sentryCocoaVersion
                extraOpts += listOf("-compiler-option", "-fmodules")
            }

            ios.deploymentTarget = Config.Cocoa.iosDeploymentTarget
            osx.deploymentTarget = Config.Cocoa.osxDeploymentTarget
            tvos.deploymentTarget = Config.Cocoa.tvosDeploymentTarget
            watchos.deploymentTarget = Config.Cocoa.watchosDeploymentTarget
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
                "-DSentryMechanismMeta=SentryMechanismMetaUnavailable",
                "-compiler-option",
                "-DSentryIntegrationProtocol=SentryIntegrationProtocolUnavailable",
                "-compiler-option",
                "-DSentryMetricsAPIDelegate=SentryMetricsAPIDelegateUnavailable"
            )
        }
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
