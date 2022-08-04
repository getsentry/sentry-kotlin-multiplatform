package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryEvent
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import NSException.Sentry.SentryEvent as NSExceptionSentryEvent

fun SentryOptions.toCocoaSentryOptions(): CocoaSentryOptions {
    val sentryAppleOptions = CocoaSentryOptions()
    sentryAppleOptions.dsn = this.dsn
    sentryAppleOptions.attachStacktrace = this.attachStackTrace
    sentryAppleOptions.beforeSend = { event ->
        dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as SentryEvent?
    }
    return sentryAppleOptions
}
