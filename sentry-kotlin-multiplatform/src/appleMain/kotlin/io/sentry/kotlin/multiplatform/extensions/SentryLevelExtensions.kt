package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.SentryLevel
import kotlinx.cinterop.convert
import platform.darwin.NSUInteger
import cocoapods.Sentry.SentryLevel as CocoaSentryLevel

//    kSentryLevelNone = 0,
//    kSentryLevelDebug = 1,
//    kSentryLevelInfo = 2,
//    kSentryLevelWarning = 3,
//    kSentryLevelError = 4,
//    kSentryLevelFatal = 5

fun SentryLevel.toCocoaSentryLevel(): CocoaSentryLevel {
    val level: Int
    when (this) {
        SentryLevel.DEBUG -> {
            level = 1
            return level.convert()
        }
        SentryLevel.INFO -> {
            level = 2
            return level.convert()
        }
        SentryLevel.WARNING -> {
            level = 3
            return level.convert()
        }
        SentryLevel.ERROR -> {
            level = 4
            return level.convert()
        }
        SentryLevel.FATAL -> {
            level = 5
            return level.convert()
        }
        else -> {
            level = 0
            return level.convert()
        }
    }
}