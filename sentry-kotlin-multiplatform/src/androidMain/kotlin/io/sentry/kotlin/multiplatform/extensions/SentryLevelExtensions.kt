package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.SentryLevel as AndroidSentryLevel

fun SentryLevel.toAndroidSentryLevel(): AndroidSentryLevel {
    when (this) {
        SentryLevel.DEBUG -> AndroidSentryLevel.DEBUG
        SentryLevel.INFO -> AndroidSentryLevel.INFO
        SentryLevel.WARNING -> AndroidSentryLevel.WARNING
        SentryLevel.ERROR -> AndroidSentryLevel.ERROR
        SentryLevel.FATAL -> AndroidSentryLevel.FATAL
    }
    // TODO: is there a default?
    throw IllegalArgumentException("Sentry Level does not exist")
}