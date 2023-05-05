object Config {
    val agpVersion = "7.4.2"
    val kotlinVersion = "1.8.0"
    val composeVersion = "1.3.1-rc01"
    val gradleMavenPublishPluginVersion = "0.18.0"

    val multiplatform = "multiplatform"
    val cocoapods = "native.cocoapods"
    val jetpackCompose = "org.jetbrains.compose"
    val gradleMavenPublishPlugin = "com.vanniktech.maven.publish"
    val androidGradle = "com.android.library"
    val kotlinSerializationPlugin = "plugin.serialization"

    object BuildPlugins {
        val buildConfig = "com.codingfeline.buildkonfig"
        val buildConfigVersion = "0.13.3"
    }

    object QualityPlugins {
        val spotless = "com.diffplug.spotless"
        val spotlessVersion = "6.11.0"
        val binaryCompatibility = "org.jetbrains.kotlinx.binary-compatibility-validator"
        val binaryCompatibilityVersion = "0.13.1"
    }

    object Libs {
        val kotlinStd = "org.jetbrains.kotlin:kotlin-stdlib"

        val sentryJavaVersion = "6.14.0"
        val sentryAndroid = "io.sentry:sentry-android:$sentryJavaVersion"
        val sentryJava = "io.sentry:sentry:$sentryJavaVersion"

        val sentryCocoaVersion = "8.4.0"
        val sentryCocoa = "Sentry"
    }

    object TestLibs {
        val kotlinCommon = "org.jetbrains.kotlin:kotlin-test-common"
        val kotlinCommonAnnotation = "org.jetbrains.kotlin:kotlin-test-annotations-common"
        val kotlinJunit = "org.jetbrains.kotlin:kotlin-test-junit"
        val kotlinCoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-RC"
        val kotlinCoroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0-RC"
        val kotlinxSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0"

        val ktorClientCore = "io.ktor:ktor-client-core:2.3.0"
        val ktorClientSerialization = "io.ktor:ktor-client-serialization:2.3.0"
        val ktorClientOkHttp = "io.ktor:ktor-client-okhttp:2.3.0"
        val ktorClientDarwin = "io.ktor:ktor-client-darwin:2.3.0"

        val roboelectric = "org.robolectric:robolectric:4.9"
        val junitKtx = "androidx.test.ext:junit-ktx:1.1.5"
    }

    object Android {
        private val sdkVersion = 33

        val minSdkVersion = 16
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
        val javaPackageName = "maven:io.sentry:sentry"
        val androidPackageName = "maven:io.sentry:sentry-android"
        val cocoaPackageName = "cocoapods:sentry-cocoa"
        val group = "io.sentry"
        val description = "SDK for sentry.io"
        val versionNameProp = "versionName"
    }
}
