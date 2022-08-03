package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.AndroidSentryLevel
import io.sentry.kotlin.multiplatform.SentryLevel

fun SentryLevel.toAndroidSentryLevel(): AndroidSentryLevel? {
    when (this) {
        SentryLevel.DEBUG -> return AndroidSentryLevel.DEBUG
        SentryLevel.INFO -> return AndroidSentryLevel.INFO
        SentryLevel.WARNING -> return AndroidSentryLevel.WARNING
        SentryLevel.ERROR -> return AndroidSentryLevel.ERROR
        SentryLevel.FATAL -> return AndroidSentryLevel.FATAL
        else -> {
            return null
        }
    }
}

fun AndroidSentryLevel.toKmpSentryLevel(): SentryLevel? {
    when (this) {
        AndroidSentryLevel.DEBUG -> return SentryLevel.DEBUG
        AndroidSentryLevel.INFO -> return SentryLevel.INFO
        AndroidSentryLevel.WARNING -> return SentryLevel.WARNING
        AndroidSentryLevel.ERROR -> return SentryLevel.ERROR
        AndroidSentryLevel.FATAL -> return SentryLevel.FATAL
        else -> {
            return null
        }
    }
}
