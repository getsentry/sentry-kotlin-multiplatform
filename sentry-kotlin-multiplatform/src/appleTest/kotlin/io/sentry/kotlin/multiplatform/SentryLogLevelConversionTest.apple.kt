package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.log.toCocoaSentryLogLevel
import io.sentry.kotlin.multiplatform.log.toKmpSentryLogLevel
import io.sentry.kotlin.multiplatform.log.SentryLogLevel
import kotlin.test.Test
import kotlin.test.assertEquals

/** Tests for Apple/Cocoa log level conversion functions. */
class SentryLogLevelConversionTestApple {

    @Test
    fun `TRACE converts to Cocoa value 0 and back`() {
        val kmp = SentryLogLevel.TRACE
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(0L, cocoa)
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `DEBUG converts to Cocoa value 1 and back`() {
        val kmp = SentryLogLevel.DEBUG
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(1L, cocoa)
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `INFO converts to Cocoa value 2 and back`() {
        val kmp = SentryLogLevel.INFO
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(2L, cocoa)
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `WARN converts to Cocoa value 3 and back`() {
        val kmp = SentryLogLevel.WARN
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(3L, cocoa)
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `ERROR converts to Cocoa value 4 and back`() {
        val kmp = SentryLogLevel.ERROR
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(4L, cocoa)
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `FATAL converts to Cocoa value 5 and back`() {
        val kmp = SentryLogLevel.FATAL
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(5L, cocoa)
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `unknown Cocoa value defaults to DEBUG`() {
        val unknownValue = 99L
        val kmpLevel = unknownValue.toKmpSentryLogLevel()

        assertEquals(SentryLogLevel.DEBUG, kmpLevel)
    }

    @Test
    fun `all log levels round-trip correctly`() {
        SentryLogLevel.entries.forEach { kmpLevel ->
            val cocoaLevel = kmpLevel.toCocoaSentryLogLevel()
            val backToKmp = cocoaLevel.toKmpSentryLogLevel()
            assertEquals(kmpLevel, backToKmp, "Round-trip failed for $kmpLevel")
        }
    }
}

