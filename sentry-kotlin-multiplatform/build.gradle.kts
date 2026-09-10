@file:OptIn(ExperimentalWasmDsl::class)

import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import io.github.frankois944.spmForKmp.swiftPackageConfig
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Config.multiplatform)
    id(Config.spmForKmp)
    id(Config.androidGradle)
    id(Config.BuildPlugins.buildConfig)
    kotlin(Config.kotlinSerializationPlugin)
    id(Config.QualityPlugins.kover)
    id(Config.QualityPlugins.binaryCompatibility)
    `maven-publish`
}

android {
    namespace = "io.sentry.kotlin.multiplatform"
    compileSdk = Config.Android.compileSdkVersion
    // Preserve the public BuildConfig class, which AGP 8 no longer generates by default.
    buildFeatures {
        buildConfig = true
    }
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

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

kotlin {
    explicitApi()
    applyDefaultHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release")
    }
    jvm()

    val appleTargets =
        listOf(
            iosArm64(),
            iosSimulatorArm64(),
            iosX64(),
            watchosArm32(),
            watchosArm64(),
            watchosX64(),
            watchosSimulatorArm64(),
            tvosArm64(),
            tvosX64(),
            tvosSimulatorArm64(),
            macosX64(),
            macosArm64()
        )
    addNoOpTargets()

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
            implementation(Config.Libs.kotlinStd)
        }

        commonTest.dependencies {
            implementation(Config.TestLibs.kotlinCoroutinesCore)
            implementation(Config.TestLibs.kotlinCoroutinesTest)
            implementation(Config.TestLibs.ktorClientCore)
            implementation(Config.TestLibs.ktorClientSerialization)
            implementation(Config.TestLibs.kotlinxSerializationJson)
            implementation(Config.TestLibs.kotlinCommon)
            implementation(Config.TestLibs.kotlinCommonAnnotation)
        }

        androidMain.dependencies {
            api(Config.Libs.sentryAndroid)
        }

        // androidUnitTest.dependencies doesn't exist
        val androidUnitTest by getting {
            dependencies {
                implementation(Config.TestLibs.roboelectric)
                implementation(Config.TestLibs.junitKtx)
                implementation(Config.TestLibs.mockitoCore)
            }
        }

        val commonJvmMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                api(Config.Libs.sentryJava)
            }
        }

        androidMain.get().dependsOn(commonJvmMain)
        jvmMain.get().dependsOn(commonJvmMain)

        val commonJvmTest by creating {
            dependsOn(commonTest.get())
            dependencies {
                implementation(Config.TestLibs.kotlinJunit)
                implementation(Config.TestLibs.ktorClientOkHttp)
            }
        }

        androidUnitTest.dependsOn(commonJvmTest)
        jvmTest.get().dependsOn(commonJvmTest)

        appleTest.dependencies {
            implementation(Config.TestLibs.ktorClientDarwin)
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

        appleTargets.forEach { target ->
            target.swiftPackageConfig(cinteropName = "sentryCocoa") {
                // Preserve the cocoapods.Sentry package used by consumers.
                packageDependencyPrefix = "cocoapods"
                minIos = Config.Cocoa.iosDeploymentTarget
                minMacos = Config.Cocoa.osxDeploymentTarget
                minTvos = Config.Cocoa.tvosDeploymentTarget
                minWatchos = Config.Cocoa.watchosDeploymentTarget
                // Avoid duplicate declarations from Kotlin/Native's "Meta" naming conflict (KT-41709).
                // Use extraOpts to pass these flags to cinterop.
                // https://youtrack.jetbrains.com/issue/KT-41709
                extraOpts =
                    listOf(
                        "-compiler-option",
                        "-DSentryMechanismMeta=SentryMechanismMetaUnavailable",
                        "-compiler-option",
                        "-DSentryIntegrationProtocol=SentryIntegrationProtocolUnavailable",
                        "-compiler-option",
                        "-DSentryMetricsAPIDelegate=SentryMetricsAPIDelegateUnavailable"
                    )
                dependency {
                    remotePackageVersion(
                        url = uri("https://github.com/getsentry/sentry-cocoa.git"),
                        version = Config.Libs.sentryCocoaVersion,
                        products = {
                            add("Sentry", exportToKotlin = true)
                        }
                    )
                }
            }

            // These private headers are self-contained; their symbols link against Sentry.framework.
            target.compilations.getByName("main") {
                cinterops.create("Sentry.Internal") {
                    includeDirs("$projectDir/src/nativeInterop/cinterop/SentryInternal")
                }
            }
        }

        val commonStub by creating {
            dependsOn(commonMain.get())
        }
        jsMain.get().dependsOn(commonStub)
        wasmJsMain.get().dependsOn(commonStub)
        linuxMain.get().dependsOn(commonStub)
        mingwMain.get().dependsOn(commonStub)
    }
}

// spm4Kmp uses aarch64-apple-watchos-simulator, which SwiftPM cannot match to the arm64 slice.
// Copy the missing Sentry.framework so cinterop can find its module map.
// Remove this workaround when spm4Kmp uses arm64 for this target.
val sentryCocoaScratchDir = layout.buildDirectory.dir("spmKmpPlugin/sentryCocoa/scratch")
val copyWatchosSimulatorSentryFramework =
    tasks.register<Copy>("copyWatchosSimulatorSentryFramework") {
        dependsOn("SwiftPackageConfigAppleSentryCocoaCompileSwiftPackageWatchosSimulatorArm64")
        // Match lazily: the compile task downloads the xcframework.
        from(sentryCocoaScratchDir.map { it.dir("artifacts/sentry-cocoa/Sentry/Sentry.xcframework") }) {
            include("watchos-*-simulator/Sentry.framework/**")
        }
        // Place Sentry.framework directly in the build products directory.
        eachFile {
            relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
        }
        includeEmptyDirs = false
        into(sentryCocoaScratchDir.map { it.dir("aarch64-apple-watchos-simulator/release") })
    }
tasks
    .matching { it.name == "SwiftPackageConfigAppleSentryCocoaGenerateCInteropDefinitionWatchosSimulatorArm64" }
    .configureEach { dependsOn(copyWatchosSimulatorSentryFramework) }

// Stub targets do not run tests. Their inherited commonTest dependencies include Ktor,
// which lacks variants for some targets. Exclude it and disable test compilation and execution.
val noOpStubTargets = listOf("js", "wasmJs", "mingwX64", "linuxArm64", "linuxX64")
configurations
    .matching { configuration ->
        noOpStubTargets.any { configuration.name.startsWith(it) } &&
            configuration.name.contains("Test")
    }.configureEach {
        exclude(group = "io.ktor")
    }
tasks
    .matching { task ->
        noOpStubTargets.any { task.name.contains(it, ignoreCase = true) } &&
            (task.name.startsWith("compileTestKotlin") || task.name.endsWith("Test"))
    }.configureEach {
        enabled = false
    }

buildkonfig {
    packageName = "io.sentry.kotlin.multiplatform"
    defaultConfigs {
        buildConfigField(STRING, "SENTRY_KMP_COCOA_SDK_NAME", Config.Sentry.kmpCocoaSdkName)
        buildConfigField(STRING, "SENTRY_KMP_JAVA_SDK_NAME", Config.Sentry.kmpJavaSdkName)
        buildConfigField(STRING, "SENTRY_KMP_ANDROID_SDK_NAME", Config.Sentry.kmpAndroidSdkName)
        buildConfigField(STRING, "SENTRY_KMP_NATIVE_ANDROID_SDK_NAME", Config.Sentry.kmpNativeAndroidSdkName)

        buildConfigField(STRING, "VERSION_NAME", project.version.toString())
        buildConfigField(STRING, "SENTRY_JAVA_PACKAGE_NAME", Config.Sentry.javaPackageName)
        buildConfigField(STRING, "SENTRY_ANDROID_PACKAGE_NAME", Config.Sentry.androidPackageName)
        buildConfigField(STRING, "SENTRY_COCOA_PACKAGE_NAME", Config.Sentry.cocoaPackageName)

        buildConfigField(STRING, "SENTRY_JAVA_VERSION", Config.Libs.sentryJavaVersion)
        buildConfigField(STRING, "SENTRY_ANDROID_VERSION", Config.Libs.sentryJavaVersion)
        buildConfigField(STRING, "SENTRY_COCOA_VERSION", Config.Libs.sentryCocoaVersion)
    }
}

private fun KotlinMultiplatformExtension.addNoOpTargets() {
    js(IR) {
        browser()
        binaries.library()
    }
    wasmJs {
        browser()
        binaries.library()
    }
    mingwX64()
    linuxArm64()
    linuxX64()
}
