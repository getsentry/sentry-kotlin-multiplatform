package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toJvmSentryLevel

actual class SentryLevelTestConverter actual constructor() {
    actual fun convert(sentryLevel: SentryLevel): SentryLevel {
        sentryLevel.toJvmSentryLevel()?.toKmpSentryLevel()?.let { return it }
        throw RuntimeException("Test Error during Jvm SentryLevel conversion")
    }
}
