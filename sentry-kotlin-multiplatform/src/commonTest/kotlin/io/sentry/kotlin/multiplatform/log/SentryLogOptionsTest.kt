package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.SentryAttributeValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

/** Tests for [SentryLogOptions] configuration and beforeSend callback. */
class SentryLogOptionsTest {

    @Test
    fun `enabled is false by default`() {
        val options = SentryLogOptions()

        assertFalse(options.enabled)
    }

    @Test
    fun `beforeSend returning null drops log`() {
        val options = SentryLogOptions()
        options.beforeSend = { null }

        val log = createTestLog()
        val result = options.beforeSend?.invoke(log)

        assertNull(result)
    }

    @Test
    fun `beforeSend modifies body`() {
        val expected = "modified body"
        val options = SentryLogOptions()
        options.beforeSend = {
            it.body = expected
            it
        }

        val log = createTestLog()
        val result = options.beforeSend?.invoke(log)

        assertEquals(expected, result?.body)
    }

    @Test
    fun `beforeSend modifies level`() {
        val expected = SentryLogLevel.FATAL
        val options = SentryLogOptions()
        options.beforeSend = {
            it.level = expected
            it
        }

        val log = createTestLog()
        val result = options.beforeSend?.invoke(log)

        assertEquals(expected, result?.level)
    }

    @Test
    fun `beforeSend modifies severityNumber`() {
        val expected = 99
        val options = SentryLogOptions()
        options.beforeSend = {
            it.severityNumber = expected
            it
        }

        val log = createTestLog()
        val result = options.beforeSend?.invoke(log)

        assertEquals(expected, result?.severityNumber)
    }

    @Test
    fun `beforeSend modifies attributes`() {
        val options = SentryLogOptions()
        options.beforeSend = {
            it.attributes.setAttribute(SentryAttributeValue.string("custom", "value"))
            it
        }

        val log = createTestLog()
        val result = options.beforeSend?.invoke(log)

        assertEquals("value", result?.attributes?.getString("custom"))
    }

    @Test
    fun `beforeSend can filter based on log level`() {
        val options = SentryLogOptions()
        options.beforeSend = { log ->
            if (log.level == SentryLogLevel.DEBUG) null else log
        }

        val debugLog = createTestLog(level = SentryLogLevel.DEBUG)
        val errorLog = createTestLog(level = SentryLogLevel.ERROR)

        assertNull(options.beforeSend?.invoke(debugLog))
        assertEquals(SentryLogLevel.ERROR, options.beforeSend?.invoke(errorLog)?.level)
    }

    private fun createTestLog(
        timestamp: Double = 1234567890.0,
        level: SentryLogLevel = SentryLogLevel.INFO,
        body: String = "test message"
    ): SentryLog = SentryLog(timestamp, level, body)
}

