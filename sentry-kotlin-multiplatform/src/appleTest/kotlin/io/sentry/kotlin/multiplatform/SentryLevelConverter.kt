package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import kotlinx.cinterop.convert

actual data class SentryLevelConverter actual constructor(val sentryLevel: SentryLevel) {
    actual fun getLevel(): Int {
        return sentryLevel.toCocoaSentryLevel().convert()
    }
}
