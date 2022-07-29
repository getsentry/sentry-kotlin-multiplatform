package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.SentryLevel as AndroidSentryLevel

fun SentryLevel.toAndroidSentryLevel(): AndroidSentryLevel {
    when (this) {
        SentryLevel.DEBUG -> return AndroidSentryLevel.DEBUG
        SentryLevel.INFO -> return AndroidSentryLevel.INFO
        SentryLevel.WARNING -> return AndroidSentryLevel.WARNING
        SentryLevel.ERROR -> return AndroidSentryLevel.ERROR
        SentryLevel.FATAL -> return AndroidSentryLevel.FATAL
    }
    // TODO: is there a default?
    throw IllegalArgumentException("Sentry Level does not exist")
}
