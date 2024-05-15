package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryOptionsCallback

public actual typealias SentryPlatformOptions = io.sentry.android.core.SentryAndroidOptions

internal actual fun SentryPlatformOptions.prepareForInit() {
    sdkVersion?.name = BuildKonfig.SENTRY_KMP_ANDROID_SDK_NAME
    sdkVersion?.version = BuildKonfig.VERSION_NAME
    if (sdkVersion?.packageSet?.none { it.name == BuildKonfig.SENTRY_ANDROID_PACKAGE_NAME } == true) {
        sdkVersion?.addPackage(BuildKonfig.SENTRY_ANDROID_PACKAGE_NAME, BuildKonfig.SENTRY_ANDROID_VERSION)
    }
}

internal actual fun SentryOptions.toPlatformOptionsConfiguration(): PlatformOptionsConfiguration =
    toAndroidSentryOptionsCallback()

internal actual fun SentryPlatformOptions.prepareForInitBridge() {
    prepareForInit()
}
