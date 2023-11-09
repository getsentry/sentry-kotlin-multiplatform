@file:OptIn(ExperimentalForeignApi::class)

package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import kotlinx.cinterop.ExperimentalForeignApi

actual class SentryLevelTestConverter actual constructor() {
    actual fun convert(sentryLevel: SentryLevel?): SentryLevel? {
        return sentryLevel?.toCocoaSentryLevel()?.toKmpSentryLevel()
    }
}
