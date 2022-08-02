package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import kotlinx.cinterop.convert
import kotlin.test.Test
import kotlin.test.assertEquals

//    kSentryLevelNone = 0,
//    kSentryLevelDebug = 1,
//    kSentryLevelInfo = 2,
//    kSentryLevelWarning = 3,
//    kSentryLevelError = 4,
//    kSentryLevelFatal = 5

class CocoaSentryLevelTest {

    @Test
    fun `convert SentryLevel to CocoaSentryLevel has proper value`() {
        val levelDebug = SentryLevel.DEBUG
        val cocoaSentryLevelDebug = levelDebug.toCocoaSentryLevel()

        val levelInfo = SentryLevel.INFO
        val cocoaSentryLevelInfo = levelInfo.toCocoaSentryLevel()

        val levelWarning = SentryLevel.WARNING
        val cocoaSentryLevelWarning = levelWarning.toCocoaSentryLevel()

        val levelError = SentryLevel.ERROR
        val cocoaSentryLevelError = levelError.toCocoaSentryLevel()

        val levelFatal = SentryLevel.FATAL
        val cocoaSentryLevelFatal = levelFatal.toCocoaSentryLevel()

        assertEquals((1).convert(), cocoaSentryLevelDebug)
        assertEquals((2).convert(), cocoaSentryLevelInfo)
        assertEquals((3).convert(), cocoaSentryLevelWarning)
        assertEquals((4).convert(), cocoaSentryLevelError)
        assertEquals((5).convert(), cocoaSentryLevelFatal)
    }
}