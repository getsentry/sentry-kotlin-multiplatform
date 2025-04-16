object Config {
    val agpVersion = "7.4.2"
    val kotlinVersion = "1.9.23"
    val composeVersion = "1.6.1"
    val gradleMavenPublishPluginVersion = "0.18.0"

    val multiplatform = "multiplatform"
    val cocoapods = "native.cocoapods"
    val jetpackCompose = "org.jetbrains.compose"
    val gradleMavenPublishPlugin = "com.vanniktech.maven.publish"
    val androidGradle = "com.android.library"
    val kotlinSerializationPlugin = "plugin.serialization"
    val dokka = "org.jetbrains.dokka"
    val dokkaVersion = "1.8.10"

    object BuildPlugins {
        val buildConfig = "com.codingfeline.buildkonfig"
        val buildConfigVersion = "0.13.3"
    }

    object QualityPlugins {
        val spotless = "com.diffplug.spotless"
        val spotlessVersion = "6.11.0"
        val kover = "org.jetbrains.kotlinx.kover"
        val koverVersion = "0.7.3"
        val detekt = "io.gitlab.arturbosch.detekt"
        val detektVersion = "1.22.0"
        val binaryCompatibility = "org.jetbrains.kotlinx.binary-compatibility-validator"
        val binaryCompatibilityVersion = "0.13.1"
    }

    object Libs {
        val kotlinStd = "org.jetbrains.kotlin:kotlin-stdlib"

        val sentryJavaVersion = "8.8.0"
        val sentryAndroid = "io.sentry:sentry-android:$sentryJavaVersion"
        val sentryJava = "io.sentry:sentry:$sentryJavaVersion"

        val sentryCocoaVersion = "8.48.0"
        val sentryCocoa = "Sentry"

        object Samples {
            val koinVersion = "3.5.2-RC1"
            val koinCore = "io.insert-koin:koin-core:$koinVersion"
            val koinAndroid = "io.insert-koin:koin-android:$koinVersion"
        }
    }

    object TestLibs {
        val kotlinCommon = "org.jetbrains.kotlin:kotlin-test-common"
        val kotlinCommonAnnotation = "org.jetbrains.kotlin:kotlin-test-annotations-common"
        val kotlinJunit = "org.jetbrains.kotlin:kotlin-test-junit"
        val kotlinCoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-RC"
        val kotlinCoroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0-RC"
        val kotlinxSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0"

        val ktorClientCore = "io.ktor:ktor-client-core:2.3.6"
        val ktorClientSerialization = "io.ktor:ktor-client-serialization:2.3.6"
        val ktorClientOkHttp = "io.ktor:ktor-client-okhttp:2.3.6"
        val ktorClientDarwin = "io.ktor:ktor-client-darwin:2.3.6"

        val roboelectric = "org.robolectric:robolectric:4.9"
        val junitKtx = "androidx.test.ext:junit-ktx:1.1.5"
        val mockitoCore = "org.mockito:mockito-core:5.4.0"
    }

    object Android {
        private val sdkVersion = 33

        val minSdkVersion = 21
        val targetSdkVersion = sdkVersion
        val compileSdkVersion = sdkVersion
    }

    object Cocoa {
        val iosDeploymentTarget = "11.0"
        val osxDeploymentTarget = "10.13"
        val tvosDeploymentTarget = "11.0"
        val watchosDeploymentTarget = "4.0"
    }

    object Sentry {
        val kmpCocoaSdkName = "sentry.cocoa.kmp"
        val kmpJavaSdkName = "sentry.java.kmp"
        val kmpAndroidSdkName = "sentry.java.android.kmp"
        val kmpNativeAndroidSdkName = "sentry.native.android.kmp"
        val javaPackageName = "maven:io.sentry:sentry"
        val androidPackageName = "maven:io.sentry:sentry-android"
        val cocoaPackageName = "cocoapods:sentry-cocoa"
        val group = "io.sentry"
        val description = "SDK for sentry.io"
        val versionNameProp = "versionName"
    }
}
