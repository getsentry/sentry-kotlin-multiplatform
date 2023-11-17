package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.nsexception.setSentryUnhandledExceptionHook
import kotlinx.cinterop.ExperimentalForeignApi

/** Convenience extension to setup unhandled exception hook */
@OptIn(ExperimentalForeignApi::class)
internal fun SentrySDK.Companion.start(configuration: (CocoaSentryOptions?) -> Unit) {
    this.startWithConfigureOptions(configuration)
    setSentryUnhandledExceptionHook()
}
