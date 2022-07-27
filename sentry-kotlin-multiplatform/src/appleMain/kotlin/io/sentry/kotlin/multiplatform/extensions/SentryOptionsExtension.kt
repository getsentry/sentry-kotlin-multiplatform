package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryOptions
import io.sentry.kotlin.multiplatform.SentryKMPOptions

fun SentryKMPOptions.toCocoaSentryOptions(): SentryOptions {
    val sentryAppleOptions = SentryOptions()
    sentryAppleOptions.dsn = this.dsn
    sentryAppleOptions.attachStacktrace = this.attachStackTrace
    return sentryAppleOptions
}