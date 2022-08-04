package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryLevel

actual class SentryLevelConverter actual constructor() {
    actual fun convert(sentryLevel: SentryLevel): SentryLevel {
        sentryLevel.toAndroidSentryLevel()?.toKmpSentryLevel()?.let { return it }
        throw RuntimeException("Test Error during Android SentryLevel conversion")
    }
}
