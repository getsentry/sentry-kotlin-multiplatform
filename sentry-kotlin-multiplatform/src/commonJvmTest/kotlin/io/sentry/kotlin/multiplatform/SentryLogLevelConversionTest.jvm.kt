package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toJvmSentryLogLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLogLevel
import io.sentry.kotlin.multiplatform.log.SentryLogLevel
import kotlin.test.Test
import kotlin.test.assertEquals

/** Tests for JVM log level conversion functions. */
class SentryLogLevelConversionTestJvm {

    @Test
    fun `TRACE converts to JVM and back`() {
        val kmp = SentryLogLevel.TRACE
        val jvm = kmp.toJvmSentryLogLevel()

        assertEquals(JvmSentryLogLevel.TRACE, jvm)
        assertEquals(kmp, jvm.toKmpSentryLogLevel())
    }

    @Test
    fun `DEBUG converts to JVM and back`() {
        val kmp = SentryLogLevel.DEBUG
        val jvm = kmp.toJvmSentryLogLevel()

        assertEquals(JvmSentryLogLevel.DEBUG, jvm)
        assertEquals(kmp, jvm.toKmpSentryLogLevel())
    }

    @Test
    fun `INFO converts to JVM and back`() {
        val kmp = SentryLogLevel.INFO
        val jvm = kmp.toJvmSentryLogLevel()

        assertEquals(JvmSentryLogLevel.INFO, jvm)
        assertEquals(kmp, jvm.toKmpSentryLogLevel())
    }

    @Test
    fun `WARN converts to JVM and back`() {
        val kmp = SentryLogLevel.WARN
        val jvm = kmp.toJvmSentryLogLevel()

        assertEquals(JvmSentryLogLevel.WARN, jvm)
        assertEquals(kmp, jvm.toKmpSentryLogLevel())
    }

    @Test
    fun `ERROR converts to JVM and back`() {
        val kmp = SentryLogLevel.ERROR
        val jvm = kmp.toJvmSentryLogLevel()

        assertEquals(JvmSentryLogLevel.ERROR, jvm)
        assertEquals(kmp, jvm.toKmpSentryLogLevel())
    }

    @Test
    fun `FATAL converts to JVM and back`() {
        val kmp = SentryLogLevel.FATAL
        val jvm = kmp.toJvmSentryLogLevel()

        assertEquals(JvmSentryLogLevel.FATAL, jvm)
        assertEquals(kmp, jvm.toKmpSentryLogLevel())
    }

    @Test
    fun `all log levels round-trip correctly`() {
        SentryLogLevel.entries.forEach { kmpLevel ->
            val jvmLevel = kmpLevel.toJvmSentryLogLevel()
            val backToKmp = jvmLevel.toKmpSentryLogLevel()
            assertEquals(kmpLevel, backToKmp, "Round-trip failed for $kmpLevel")
        }
    }
}

