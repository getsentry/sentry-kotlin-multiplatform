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
    // Simple API with attributes tests
    // ========================

    @Test
    fun `trace with message and attributes calls sendLog with TRACE level`() {
        val logger = TestSentryLogger()

        logger.trace("test message") {
            set("key", "value")
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.TRACE, logger.logs[0].level)
        assertEquals("test message", logger.logs[0].formatted.body)
    }

    @Test
    fun `debug with message and attributes calls sendLog with DEBUG level`() {
        val logger = TestSentryLogger()

        logger.debug("test message") {
            set("key", "value")
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.DEBUG, logger.logs[0].level)
    }

    @Test
    fun `info with message and attributes calls sendLog with INFO level`() {
        val logger = TestSentryLogger()

        logger.info("test message") {
            set("key", "value")
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.INFO, logger.logs[0].level)
    }

    @Test
    fun `warn with message and attributes calls sendLog with WARN level`() {
        val logger = TestSentryLogger()

        logger.warn("test message") {
            set("key", "value")
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.WARN, logger.logs[0].level)
    }

    @Test
    fun `error with message and attributes calls sendLog with ERROR level`() {
        val logger = TestSentryLogger()

        logger.error("test message") {
            set("key", "value")
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.ERROR, logger.logs[0].level)
    }

    @Test
    fun `fatal with message and attributes calls sendLog with FATAL level`() {
        val logger = TestSentryLogger()

        logger.fatal("test message") {
            set("key", "value")
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.FATAL, logger.logs[0].level)
    }

    @Test
    fun `message with attributes includes custom attributes`() {
        val logger = TestSentryLogger()

        logger.info("Rate limit reached") {
            set("endpoint", "/api/results/")
            set("isEnterprise", false)
        }

        val attrs = logger.logs[0].formatted.attributes
        assertEquals("Rate limit reached", logger.logs[0].formatted.body)
        assertEquals("/api/results/", attrs["endpoint"]?.stringOrNull)
        assertEquals(false, attrs["isEnterprise"]?.booleanOrNull)
    }

    @Test
    fun `message with attributes supports all attribute types`() {
        val logger = TestSentryLogger()

        logger.error("Failed to process payment") {
            set("orderId", "order_123")
            set("amount", 99.99)
            set("retries", 3)
            set("isRetryable", true)
        }

        val attrs = logger.logs[0].formatted.attributes
        assertEquals("order_123", attrs["orderId"]?.stringOrNull)
        assertEquals(99.99, attrs["amount"]?.doubleOrNull)
        assertEquals(3L, attrs["retries"]?.longOrNull)
        assertEquals(true, attrs["isRetryable"]?.booleanOrNull)
    }

    @Test
    fun `message with attributes has no template attributes`() {
        val logger = TestSentryLogger()

        logger.fatal("Database connection pool exhausted") {
            set("database", "users")
            set("activeConnections", 100)
        }

        val attrs = logger.logs[0].formatted.attributes
        assertEquals(null, attrs["sentry.message.template"])
        assertEquals("users", attrs["database"]?.stringOrNull)
        assertEquals(100L, attrs["activeConnections"]?.longOrNull)
    }

    @Test
    fun `trace with args and attributes calls sendLog with TRACE level`() {
        val logger = TestSentryLogger()

        logger.trace("User %s logged in", "alice") {
            set("region", "us-east")
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.TRACE, logger.logs[0].level)
        assertEquals("User alice logged in", logger.logs[0].formatted.body)
        assertEquals("us-east", logger.logs[0].formatted.attributes["region"]?.stringOrNull)
    }

    @Test
    fun `debug with args and attributes calls sendLog with DEBUG level`() {
        val logger = TestSentryLogger()

        logger.debug("Cache %s for key: %s", "hit", "user_123") {
            set("ttl", 300)
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.DEBUG, logger.logs[0].level)
        assertEquals("Cache hit for key: user_123", logger.logs[0].formatted.body)
    }

    @Test
    fun `info with args and attributes calls sendLog with INFO level`() {
        val logger = TestSentryLogger()

        logger.info("User %s authenticated", "bob") {
            set("method", "oauth2")
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.INFO, logger.logs[0].level)
    }

    @Test
    fun `warn with args and attributes calls sendLog with WARN level`() {
        val logger = TestSentryLogger()

        logger.warn("Rate limit at %s%%", 80) {
            set("endpoint", "/api")
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.WARN, logger.logs[0].level)
    }

    @Test
    fun `error with args and attributes calls sendLog with ERROR level`() {
        val logger = TestSentryLogger()

        logger.error("Failed order %s", "order_123") {
            set("retries", 3)
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.ERROR, logger.logs[0].level)
    }

    @Test
    fun `fatal with args and attributes calls sendLog with FATAL level`() {
        val logger = TestSentryLogger()

        logger.fatal("Pool exhausted on %s", "db-primary") {
            set("connections", 100)
        }

        assertEquals(1, logger.logs.size)
        assertEquals(SentryLogLevel.FATAL, logger.logs[0].level)
    }

    @Test
    fun `message with args and attributes includes template and custom attributes`() {
        val logger = TestSentryLogger()

        logger.info("User %s performed %s", "alice", "checkout") {
            set("orderId", "order_456")
            set("amount", 49.99)
        }

        val attrs = logger.logs[0].formatted.attributes
        assertEquals("User alice performed checkout", logger.logs[0].formatted.body)
        assertEquals("User %s performed %s", attrs["sentry.message.template"]?.stringOrNull)
        assertEquals("alice", attrs["sentry.message.parameter.0"]?.stringOrNull)
        assertEquals("checkout", attrs["sentry.message.parameter.1"]?.stringOrNull)
        assertEquals("order_456", attrs["orderId"]?.stringOrNull)
        assertEquals(49.99, attrs["amount"]?.doubleOrNull)
    }

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
