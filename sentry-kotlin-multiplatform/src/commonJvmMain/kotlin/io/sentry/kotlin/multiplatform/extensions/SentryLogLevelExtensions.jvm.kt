package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.SentryLogLevel

/**
 * Converts KMP [SentryLogLevel] to Java SDK's SentryLogLevel.
 */
internal fun SentryLogLevel.toJvmSentryLogLevel(): io.sentry.SentryLogLevel {
    return when (this) {
        SentryLogLevel.TRACE -> io.sentry.SentryLogLevel.TRACE
        SentryLogLevel.DEBUG -> io.sentry.SentryLogLevel.DEBUG
        SentryLogLevel.INFO -> io.sentry.SentryLogLevel.INFO
        SentryLogLevel.WARN -> io.sentry.SentryLogLevel.WARN
        SentryLogLevel.ERROR -> io.sentry.SentryLogLevel.ERROR
        SentryLogLevel.FATAL -> io.sentry.SentryLogLevel.FATAL
    }
}

/**
 * Converts Java SDK's SentryLogLevel to KMP [SentryLogLevel].
 */
internal fun io.sentry.SentryLogLevel.toKmpSentryLogLevel(): SentryLogLevel {
    return when (this) {
        io.sentry.SentryLogLevel.TRACE -> SentryLogLevel.TRACE
        io.sentry.SentryLogLevel.DEBUG -> SentryLogLevel.DEBUG
        io.sentry.SentryLogLevel.INFO -> SentryLogLevel.INFO
        io.sentry.SentryLogLevel.WARN -> SentryLogLevel.WARN
        io.sentry.SentryLogLevel.ERROR -> SentryLogLevel.ERROR
        io.sentry.SentryLogLevel.FATAL -> SentryLogLevel.FATAL
    }
}
