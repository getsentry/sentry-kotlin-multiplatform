package io.sentry.kotlin.multiplatform

import PrivateSentrySDKOnly.Sentry.PrivateSentrySDKOnly
import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.nsexception.setSentryUnhandledExceptionHook

/** Convenience extension to setup unhandled exception hook */
internal fun SentrySDK.Companion.start(configuration: (CocoaSentryOptions?) -> Unit) {
    startWithConfigureOptions(configuration)
    setSentryUnhandledExceptionHook()
}

internal fun SentrySDK.Companion.start(options: CocoaSentryOptions) {
    startWithOptions(options)
    setSentryUnhandledExceptionHook()
}

internal fun setSdkVersionAndName() {
    PrivateSentrySDKOnly.setSdkName(BuildKonfig.SENTRY_KMP_COCOA_SDK_NAME, BuildKonfig.VERSION_NAME)
}