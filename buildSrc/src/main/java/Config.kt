object Config {
    object Libs {
        val sentryCocoa = "Sentry"

        object Samples {
            val koinVersion = "3.5.2-RC1"
            val koinCore = "io.insert-koin:koin-core:$koinVersion"
            val koinAndroid = "io.insert-koin:koin-android:$koinVersion"
        }
    }

    object Android {
        private val sdkVersion = 33

        val minSdkVersion = 19
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
