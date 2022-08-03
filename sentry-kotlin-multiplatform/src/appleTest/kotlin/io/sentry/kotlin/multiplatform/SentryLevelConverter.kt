package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel

actual data class SentryLevelConverter actual constructor(val sentryLevel: SentryLevel) {
    actual fun getLevel(): SentryLevel {
        sentryLevel.toCocoaSentryLevel().toKmpSentryLevel()?.let { return it }
        throw RuntimeException("Test Error during Cocoa SentryLevel conversion")
    }
}
