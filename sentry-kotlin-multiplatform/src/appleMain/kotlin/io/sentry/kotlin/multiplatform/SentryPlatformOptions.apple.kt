package io.sentry.kotlin.multiplatform

import PrivateSentrySDKOnly.Sentry.PrivateSentrySDKOnly

internal actual fun SentryPlatformOptions.prepareForInit() {
    PrivateSentrySDKOnly.setSdkName(BuildKonfig.SENTRY_KMP_COCOA_SDK_NAME, BuildKonfig.VERSION_NAME)
}
