package io.sentry.kotlin.multiplatform.log

import kotlinx.cinterop.convert
import kotlin.test.Test
import kotlin.test.assertEquals
import cocoapods.Sentry.SentryLogLevel as CocoaSentryLogLevel

/** Tests for Apple/Cocoa log level conversion functions in SentryLogConverters.apple.kt. */
class SentryLogConvertersTest {
    @Test
    fun `TRACE converts to Cocoa value 0 and back`() {
        val kmp = SentryLogLevel.TRACE
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(0, cocoa.convert<Int>())
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `DEBUG converts to Cocoa value 1 and back`() {
        val kmp = SentryLogLevel.DEBUG
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(1, cocoa.convert<Int>())
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `INFO converts to Cocoa value 2 and back`() {
        val kmp = SentryLogLevel.INFO
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(2, cocoa.convert<Int>())
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `WARN converts to Cocoa value 3 and back`() {
        val kmp = SentryLogLevel.WARN
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(3, cocoa.convert<Int>())
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `ERROR converts to Cocoa value 4 and back`() {
        val kmp = SentryLogLevel.ERROR
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(4, cocoa.convert<Int>())
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `FATAL converts to Cocoa value 5 and back`() {
        val kmp = SentryLogLevel.FATAL
        val cocoa = kmp.toCocoaSentryLogLevel()

        assertEquals(5, cocoa.convert<Int>())
        assertEquals(kmp, cocoa.toKmpSentryLogLevel())
    }

    @Test
    fun `unknown Cocoa value defaults to DEBUG`() {
        val unknownValue: CocoaSentryLogLevel = 99.convert()
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
