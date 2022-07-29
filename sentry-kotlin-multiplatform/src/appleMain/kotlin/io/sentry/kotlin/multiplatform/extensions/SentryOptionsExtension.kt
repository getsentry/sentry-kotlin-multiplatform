package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryEvent
import cocoapods.Sentry.SentryOptions
import io.sentry.kotlin.multiplatform.SentryKMPOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import NSException.Sentry.SentryEvent as NSExceptionSentryEvent

fun SentryKMPOptions.toCocoaSentryOptions(): SentryOptions {
    val sentryAppleOptions = SentryOptions()
    sentryAppleOptions.dsn = this.dsn
    sentryAppleOptions.attachStacktrace = this.attachStackTrace
    sentryAppleOptions.beforeSend = { event ->
        dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as SentryEvent?
    }
    return sentryAppleOptions
}
