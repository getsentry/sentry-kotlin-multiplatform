package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryLevel
import io.sentry.kotlin.multiplatform.SentryLevel

fun SentryLevel.toJvmSentryLevel(): JvmSentryLevel? {
    when (this) {
        SentryLevel.DEBUG -> return JvmSentryLevel.DEBUG
        SentryLevel.INFO -> return JvmSentryLevel.INFO
        SentryLevel.WARNING -> return JvmSentryLevel.WARNING
        SentryLevel.ERROR -> return JvmSentryLevel.ERROR
        SentryLevel.FATAL -> return JvmSentryLevel.FATAL
        else -> {
            return null
        }
    }
}

fun JvmSentryLevel.toKmpSentryLevel(): SentryLevel? {
    when (this) {
        JvmSentryLevel.DEBUG -> return SentryLevel.DEBUG
        JvmSentryLevel.INFO -> return SentryLevel.INFO
        JvmSentryLevel.WARNING -> return SentryLevel.WARNING
        JvmSentryLevel.ERROR -> return SentryLevel.ERROR
        JvmSentryLevel.FATAL -> return SentryLevel.FATAL
        else -> {
            return null
        }
    }
}
