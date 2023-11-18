package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toJvmSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel

actual class SentryLevelTestConverter actual constructor() {
  actual fun convert(sentryLevel: SentryLevel?): SentryLevel? {
    return sentryLevel?.toJvmSentryLevel()?.toKmpSentryLevel()
  }
}
