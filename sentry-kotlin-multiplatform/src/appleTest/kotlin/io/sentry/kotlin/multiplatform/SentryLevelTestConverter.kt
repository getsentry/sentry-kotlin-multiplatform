package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel

actual class SentryLevelTestConverter actual constructor() {
    actual fun convert(sentryLevel: SentryLevel): SentryLevel {
        sentryLevel.toCocoaSentryLevel().toKmpSentryLevel()?.let { return it }
        throw RuntimeException("Test Error during Cocoa SentryLevel conversion")
    }
}
