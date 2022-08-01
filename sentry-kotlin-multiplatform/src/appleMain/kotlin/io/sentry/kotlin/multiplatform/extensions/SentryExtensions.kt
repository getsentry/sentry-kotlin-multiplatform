package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.setSentryUnhandledExceptionHook

/**
 * Sentry initialization with an option configuration handler.
 *
 * @param configuration Options configuration handler.
 */
fun Sentry.start(configuration: (SentryOptions) -> Unit) {
    val options = SentryOptions()
    configuration.invoke(options)
    SentrySDK.startWithOptionsObject(options.toCocoaSentryOptions())
    setSentryUnhandledExceptionHook()
}
