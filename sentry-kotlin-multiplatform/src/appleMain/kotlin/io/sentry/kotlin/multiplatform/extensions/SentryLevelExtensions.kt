package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.SentryLevel
import kotlinx.cinterop.convert
import cocoapods.Sentry.SentryLevel as CocoaSentryLevel

internal fun SentryLevel.toCocoaSentryLevel(): CocoaSentryLevel {
    return this.toInt().convert()
}

internal fun CocoaSentryLevel.toKmpSentryLevel(): SentryLevel? {
    return SentryLevel.fromInt(this.toInt())
}
