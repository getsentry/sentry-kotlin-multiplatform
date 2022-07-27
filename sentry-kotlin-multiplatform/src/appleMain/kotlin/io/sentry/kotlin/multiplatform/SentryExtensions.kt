package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK

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