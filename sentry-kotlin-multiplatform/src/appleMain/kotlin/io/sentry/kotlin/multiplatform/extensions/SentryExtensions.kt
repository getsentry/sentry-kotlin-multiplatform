package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryBridge
import io.sentry.kotlin.multiplatform.SentryKMPOptions

/**
 * Sentry initialization with an option configuration handler.
 *
 * @param configuration Options configuration handler.
 */
fun Sentry.start(configuration: (SentryKMPOptions) -> Unit) {
    val options = SentryKMPOptions()
    configuration.invoke(options)
    SentrySDK.startWithOptionsObject(SentryBridge.convertToSentryAppleOptions(options))
}