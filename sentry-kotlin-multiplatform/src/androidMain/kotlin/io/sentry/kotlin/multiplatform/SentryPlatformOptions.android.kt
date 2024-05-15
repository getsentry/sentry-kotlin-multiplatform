package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryOptionsCallback

public actual typealias SentryPlatformOptions = io.sentry.android.core.SentryAndroidOptions

internal actual fun SentryPlatformOptions.prepareForInit() {
    sdkVersion?.name = BuildKonfig.SENTRY_KMP_ANDROID_SDK_NAME
    sdkVersion?.version = BuildKonfig.VERSION_NAME
}

internal actual fun SentryOptions.toPlatformOptionsConfiguration(): PlatformOptionsConfiguration =
    toAndroidSentryOptionsCallback()

internal actual fun SentryPlatformOptions.prepareForInitBridge() {
    prepareForInit()
}
