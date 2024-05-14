package io.sentry.kotlin.multiplatform

internal actual fun SentryPlatformOptions.prepareForInit() {
    sdkVersion?.name = BuildKonfig.SENTRY_KMP_ANDROID_SDK_NAME
    sdkVersion?.version = BuildKonfig.VERSION_NAME
}
