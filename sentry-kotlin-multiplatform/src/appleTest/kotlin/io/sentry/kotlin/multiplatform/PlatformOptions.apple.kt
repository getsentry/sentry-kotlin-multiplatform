package io.sentry.kotlin.multiplatform

import PrivateSentrySDKOnly.Sentry.PrivateSentrySDKOnly
import kotlin.test.assertEquals

actual fun SentryPlatformOptions.assertSdkNameAndVersion() {
    assertEquals(BuildKonfig.SENTRY_KMP_COCOA_SDK_NAME, PrivateSentrySDKOnly.getSdkName())
    assertEquals(BuildKonfig.VERSION_NAME, PrivateSentrySDKOnly.getSdkVersionString())
}
