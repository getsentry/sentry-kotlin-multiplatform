object Config {
    object Libs {
        val sentryJavaVersion = "8.11.1"
        val sentryAndroid = "io.sentry:sentry-android:$sentryJavaVersion"
        val sentryJava = "io.sentry:sentry:$sentryJavaVersion"

        val sentryCocoaVersion = "8.49.1"
        val sentryCocoa = "Sentry"
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
