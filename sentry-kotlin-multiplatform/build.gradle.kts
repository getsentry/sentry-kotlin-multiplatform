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
    // AGP 8 disables BuildConfig generation by default; keep it on to preserve
    // the previously published Android public API surface.
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
                // Keep the legacy Kotlin CocoaPods `cocoapods.Sentry.*` import prefix so
                // published klib symbols stay identical.
                packageDependencyPrefix = "cocoapods"
                minIos = Config.Cocoa.iosDeploymentTarget
                minMacos = Config.Cocoa.osxDeploymentTarget
                minTvos = Config.Cocoa.tvosDeploymentTarget
                minWatchos = Config.Cocoa.watchosDeploymentTarget
                // KT-41709: Sentry classes with "Meta" in the name (e.g. SentryMechanismMeta) are
                // otherwise declared twice. Must be extraOpts — compilerOpts only populates the
                // generated def's clang flags. https://youtrack.jetbrains.com/issue/KT-41709
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

            // The private `Sentry.Internal` cinterop has self-contained headers; its symbols
            // resolve at link time against the Sentry framework regardless of how it is delivered.
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

// spm4Kmp compiles watchosSimulatorArm64 with `--triple aarch64-apple-watchos-simulator`
// (1.9.2 used `arm64`), and SwiftPM does not treat `aarch64` as `arm64` when matching binary
// xcframework slices, so Sentry.framework is never copied into the build products directory and
// the cinterop definition task fails with "Module map file not found for module: Sentry".
// Copy the watchOS simulator slice there manually until spm4Kmp maps this target back to `arm64`.
val sentryCocoaScratchDir = layout.buildDirectory.dir("spmKmpPlugin/sentryCocoa/scratch")
val copyWatchosSimulatorSentryFramework =
    tasks.register<Copy>("copyWatchosSimulatorSentryFramework") {
        dependsOn("SwiftPackageConfigAppleSentryCocoaCompileSwiftPackageWatchosSimulatorArm64")
        // Select the slice with a pattern: the xcframework doesn't exist yet when Gradle
        // computes task dependencies, only after the compile task resolves the Swift package.
        from(sentryCocoaScratchDir.map { it.dir("artifacts/sentry-cocoa/Sentry/Sentry.xcframework") }) {
            include("watchos-*-simulator/Sentry.framework/**")
        }
        // Strip the slice directory segment so the framework lands directly in the products dir.
        eachFile {
            relativePath = RelativePath(true, *relativePath.segments.drop(1).toTypedArray())
        }
        includeEmptyDirs = false
        into(sentryCocoaScratchDir.map { it.dir("aarch64-apple-watchos-simulator/release") })
    }
tasks
    .matching { it.name == "SwiftPackageConfigAppleSentryCocoaGenerateCInteropDefinitionWatchosSimulatorArm64" }
    .configureEach { dependsOn(copyWatchosSimulatorSentryFramework) }

// The js/wasmJs/linux/mingw targets ship as no-op stubs and run no tests. Kotlin
// 2.2.20's shared `web` source set wires their test compilations to commonTest, whose
// Ktor dependency has no wasm (and limited native) variants, which breaks dependency
// resolution. Exclude Ktor from those test classpaths and disable their test
// compile/run tasks so no test sources are compiled for these stub targets.
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
