package io.sentry.kotlin.multiplatform.log

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BaseSentryLoggerTest {
    @Test
    fun `trace with message calls sendLog with TRACE level`() {
        val logger = TestSentryLogger()

        logger.trace("test message")

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.TRACE, logger.logs[0].level)
        assertEquals("test message", logger.logs[0].formatted.body)
    }

    @Test
    fun `debug with message calls sendLog with DEBUG level`() {
        val logger = TestSentryLogger()

        logger.debug("test message")

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.DEBUG, logger.logs[0].level)
    }

    @Test
    fun `info with message calls sendLog with INFO level`() {
        val logger = TestSentryLogger()

        logger.info("test message")

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.INFO, logger.logs[0].level)
    }

    @Test
    fun `warn with message calls sendLog with WARN level`() {
        val logger = TestSentryLogger()

        logger.warn("test message")

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.WARN, logger.logs[0].level)
    }

    @Test
    fun `error with message calls sendLog with ERROR level`() {
        val logger = TestSentryLogger()

        logger.error("test message")

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.ERROR, logger.logs[0].level)
    }

    @Test
    fun `fatal with message calls sendLog with FATAL level`() {
        val logger = TestSentryLogger()

        logger.fatal("test message")

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.FATAL, logger.logs[0].level)
    }

    @Test
    fun `simple message with args formats correctly`() {
        val logger = TestSentryLogger()

        logger.info("User %s logged in from %s", "alice", "192.168.1.1")

        assertEquals("User alice logged in from 192.168.1.1", logger.logs[0].formatted.body)
    }

    @Test
    fun `simple message with args adds template info to attributes`() {
        val logger = TestSentryLogger()

        logger.info("User %s logged in", "bob")

        val attrs = logger.logs[0].formatted.attributes
        assertEquals("User %s logged in", attrs["sentry.message.template"]?.stringOrNull)
        assertEquals("bob", attrs["sentry.message.parameter.0"]?.stringOrNull)
    }

    @Test
    fun `simple message without args has no template attributes`() {
        val logger = TestSentryLogger()

        logger.info("Plain message")

        val attrs = logger.logs[0].formatted.attributes
        assertTrue(attrs.isEmpty())
    }

    // ========================
    // Builder API tests
    // ========================

    @Test
    fun `trace with builder calls sendLog with TRACE level`() {
        val logger = TestSentryLogger()

        logger.trace { message("test") }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.TRACE, logger.logs[0].level)
    }

    @Test
    fun `debug with builder calls sendLog with DEBUG level`() {
        val logger = TestSentryLogger()

        logger.debug { message("test") }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.DEBUG, logger.logs[0].level)
    }

    @Test
    fun `info with builder calls sendLog with INFO level`() {
        val logger = TestSentryLogger()

        logger.info { message("test") }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.INFO, logger.logs[0].level)
    }

    @Test
    fun `warn with builder calls sendLog with WARN level`() {
        val logger = TestSentryLogger()

        logger.warn { message("test") }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.WARN, logger.logs[0].level)
    }

    @Test
    fun `error with builder calls sendLog with ERROR level`() {
        val logger = TestSentryLogger()

        logger.error { message("test") }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.ERROR, logger.logs[0].level)
    }

    @Test
    fun `fatal with builder calls sendLog with FATAL level`() {
        val logger = TestSentryLogger()

        logger.fatal { message("test") }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.FATAL, logger.logs[0].level)
    }

    @Test
    fun `builder with message and args formats correctly`() {
        val logger = TestSentryLogger()

        logger.info {
            message("Hello %s", "world")
        }

        assertEquals("Hello world", logger.logs[0].formatted.body)
    }

    @Test
    fun `builder with custom attributes includes them`() {
        val logger = TestSentryLogger()

        logger.info {
            message("test")
            attributes {
                this["user.id"] = "123"
                this["request.duration"] = 500
            }
        }

        val attrs = logger.logs[0].formatted.attributes
        assertEquals("123", attrs["user.id"]?.stringOrNull)
        assertEquals(500L, attrs["request.duration"]?.longOrNull)
    }

    @Test
    fun `builder with message args and custom attributes merges all`() {
        val logger = TestSentryLogger()

        logger.info {
            message("User %s", "alice")
            attributes {
                this["custom"] = "value"
            }
        }

        val attrs = logger.logs[0].formatted.attributes
        assertEquals("User %s", attrs["sentry.message.template"]?.stringOrNull)
        assertEquals("alice", attrs["sentry.message.parameter.0"]?.stringOrNull)
        assertEquals("value", attrs["custom"]?.stringOrNull)
    }

    // ========================
    // Edge cases
    // ========================

    @Test
    fun `builder without message does not call sendLog`() {
        val logger = TestSentryLogger()

        logger.info {
            attributes {
                this["key"] = "value"
            }
        }

        assertTrue(logger.logs.isEmpty())
    }

    @Test
    fun `multiple log calls accumulate`() {
        val logger = TestSentryLogger()

        logger.trace("one")
        logger.debug("two")
        logger.info("three")

        assertEquals(3, logger.logs.size)
        assertEquals(SentryLogLevel.TRACE, logger.logs[0].level)
        assertEquals(SentryLogLevel.DEBUG, logger.logs[1].level)
        assertEquals(SentryLogLevel.INFO, logger.logs[2].level)
    }

    @Test
    fun `all log levels are distinct`() {
        val logger = TestSentryLogger()

        logger.trace("trace")
        logger.debug("debug")
        logger.info("info")
        logger.warn("warn")
        logger.error("error")
        logger.fatal("fatal")

        assertEquals(6, logger.logs.size)
        assertEquals(
            listOf(
                SentryLogLevel.TRACE,
                SentryLogLevel.DEBUG,
                SentryLogLevel.INFO,
                SentryLogLevel.WARN,
                SentryLogLevel.ERROR,
                SentryLogLevel.FATAL
            ),
            logger.logs.map { it.level }
        )
    }
}

/**
 * Test implementation of [BaseSentryLogger] that captures all logs for verification.
 */
private class TestSentryLogger : BaseSentryLogger(::DefaultSentryLogBuilder) {
    data class CapturedLog(val level: SentryLogLevel, val formatted: FormattedLog)

    val logs = mutableListOf<CapturedLog>()

    override fun sendLog(level: SentryLogLevel, formatted: FormattedLog) {
        logs.add(CapturedLog(level, formatted))
    }
}
