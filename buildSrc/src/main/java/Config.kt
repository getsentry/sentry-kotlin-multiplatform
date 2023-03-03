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

    object QualityPlugins {
        val spotless = "com.diffplug.spotless"
        val spotlessVersion = "6.11.0"
    }

    object Libs {
        val kotlinStd = "org.jetbrains.kotlin:kotlin-stdlib"

        val sentryJavaVersion = "6.14.0"
        val sentryAndroid = "io.sentry:sentry-android:$sentryJavaVersion"
        val sentryJava = "io.sentry:sentry:$sentryJavaVersion"

        val sentryCocoaVersion = "~> 8.2.0"
        val sentryCocoa = "Sentry"
    }

    object TestLibs {
        val kotlinCommon = "org.jetbrains.kotlin:kotlin-test-common"
        val kotlinCommonAnnotation = "org.jetbrains.kotlin:kotlin-test-annotations-common"
        val kotlinJunit = "org.jetbrains.kotlin:kotlin-test-junit"
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
}
