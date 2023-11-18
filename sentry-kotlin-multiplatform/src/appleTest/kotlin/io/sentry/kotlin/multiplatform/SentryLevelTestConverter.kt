package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel

actual class SentryLevelTestConverter actual constructor() {
  actual fun convert(sentryLevel: SentryLevel?): SentryLevel? {
    return sentryLevel?.toCocoaSentryLevel()?.toKmpSentryLevel()
  }
}
