package io.sentry.kotlin.multiplatform.integrationTest

import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryEvent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SentryInitTest {

    private val dsn = "http://key@localhost/proj"

    @Test
    fun `captureMessage sends the correct message`() {
        val expected = "test"
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                assertEquals(event.message?.formatted, expected)
                null
            }
        }

        Sentry.captureMessage(expected)
    }

    @Test
    fun `captureException sends the correct exceptions`() {
        val capturedEvents = mutableListOf<SentryEvent>()
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                capturedEvents.add(event)
                null
            }
        }

        Sentry.captureException(RuntimeException("test"))
        Sentry.captureException(RuntimeException("test2"))

        // contains the events from the previous two calls
        assertEquals(2, capturedEvents.size)

        // the first event
        val event = capturedEvents[0]
        event.exceptions.first().type?.let { assertTrue(it.contains("RuntimeException")) }
        assertEquals("test", event.exceptions?.first()?.value)

        // the second event
        val event2 = capturedEvents[1]
        event2.exceptions.first().type?.let { assertTrue(it.contains("RuntimeException")) }
        assertEquals("test2", event2.exceptions?.first()?.value)
    }

    @Test
    fun `capturing no events is correct`() {
        val capturedEvents = mutableListOf<SentryEvent>()
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                capturedEvents.add(event)
                null
            }
        }

        assertEquals(0, capturedEvents.size)
    }

    @Test
    fun `scope is correct when modified with capturing an event`() {
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                assertEquals("valueABC", event.tags["testABC"])
                null
            }
        }

        Sentry.captureException(RuntimeException("test")) {
            it.setTag("testABC", "valueABC")
        }
    }

    @Test
    fun `scope is correct when modified with capturing an event and then capturing another event`() {
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                assertEquals("valueABC", event.tags["testABC"])
                null
            }
        }

        Sentry.captureException(RuntimeException("test")) {
            it.setTag("testABC", "valueABC")
        }

        Sentry.captureException(RuntimeException("test2")) {
            it.setTag("testABC", "valueABC")
        }
    }

    @Test
    fun `global scope is correct`() {
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                assertEquals("valueABC", event.tags["testABC"])
                null
            }
        }

        Sentry.configureScope {
            it.setTag("testABC", "valueABC")
        }

        Sentry.captureException(RuntimeException("test"))
    }

    @Test
    fun `local scope overrides global scope when capturing an event and then capturing another event`() {
        Sentry.init {
            it.dsn = dsn
            it.beforeSend = { event ->
                assertEquals("valueABC", event.tags["testABC"])
                null
            }
        }

        Sentry.configureScope {
            it.setTag("testABC", "valueABC")
        }

        Sentry.captureException(RuntimeException("test")) {
            it.setTag("testABC2", "valueABC2")
        }
    }
}
