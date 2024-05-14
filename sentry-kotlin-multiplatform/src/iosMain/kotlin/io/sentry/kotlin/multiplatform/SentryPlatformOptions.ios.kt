package io.sentry.kotlin.multiplatform

import PrivateSentrySDKOnly.Sentry.PrivateSentrySDKOnly

public actual typealias SentryPlatformOptions = cocoapods.Sentry.SentryOptions

internal actual fun SentryPlatformOptions.prepareForInit() {
    PrivateSentrySDKOnly.setSdkName(BuildKonfig.SENTRY_KMP_COCOA_SDK_NAME, BuildKonfig.VERSION_NAME)
}