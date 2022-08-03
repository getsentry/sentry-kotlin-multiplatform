package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryLevel

actual data class SentryLevelConverter actual constructor(val sentryLevel: SentryLevel) {
    actual fun getLevel(): Int {
        when (sentryLevel.toAndroidSentryLevel()) {
            AndroidSentryLevel.DEBUG -> return SentryLevelNumConstants.DEBUG_LEVEL
            AndroidSentryLevel.INFO -> return SentryLevelNumConstants.INFO_LEVEL
            AndroidSentryLevel.WARNING -> return SentryLevelNumConstants.WARNING_LEVEL
            AndroidSentryLevel.ERROR -> return SentryLevelNumConstants.ERROR_LEVEL
            AndroidSentryLevel.FATAL -> return SentryLevelNumConstants.FATAL_LEVEL
            else -> {
                // We denote 0 as null for test purposes
                return 0
            }

        }
    }
}
