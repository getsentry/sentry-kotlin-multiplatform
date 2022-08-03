package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryLevel

actual data class SentryLevelConverter actual constructor(val sentryLevel: SentryLevel) {
    actual fun getLevel(): SentryLevel {
        sentryLevel.toAndroidSentryLevel()?.toKmpSentryLevel()?.let { return it }
        throw RuntimeException("Test Error during Android SentryLevel conversion")
    }
}
