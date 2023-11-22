package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.nsexception.setSentryUnhandledExceptionHook

/** Convenience extension to setup unhandled exception hook */
internal fun SentrySDK.Companion.start(configuration: (CocoaSentryOptions?) -> Unit) {
  this.startWithConfigureOptions(configuration)
  setSentryUnhandledExceptionHook()
}
