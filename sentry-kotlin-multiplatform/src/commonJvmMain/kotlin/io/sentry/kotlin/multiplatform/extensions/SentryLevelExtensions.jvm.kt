package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryLevel
import io.sentry.kotlin.multiplatform.SentryLevel

internal fun SentryLevel.toJvmSentryLevel(): JvmSentryLevel? {
    return when (this) {
        SentryLevel.DEBUG -> JvmSentryLevel.DEBUG
        SentryLevel.INFO -> JvmSentryLevel.INFO
        SentryLevel.WARNING -> JvmSentryLevel.WARNING
        SentryLevel.ERROR -> JvmSentryLevel.ERROR
        SentryLevel.FATAL -> JvmSentryLevel.FATAL
    }
}

internal fun JvmSentryLevel.toKmpSentryLevel(): SentryLevel? {
    return when (this) {
        JvmSentryLevel.DEBUG -> SentryLevel.DEBUG
        JvmSentryLevel.INFO -> SentryLevel.INFO
        JvmSentryLevel.WARNING -> SentryLevel.WARNING
        JvmSentryLevel.ERROR -> SentryLevel.ERROR
        JvmSentryLevel.FATAL -> SentryLevel.FATAL
    }
}
