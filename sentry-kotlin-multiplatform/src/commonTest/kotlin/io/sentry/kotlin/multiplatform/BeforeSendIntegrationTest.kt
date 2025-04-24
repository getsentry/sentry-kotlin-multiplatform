package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BeforeSendIntegrationTest {
    private val sentryEventConfigurator = SentryEventConfigurator()

    @Test
    fun `event is not null if KMP beforeSend option is null`() {
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
        }
        assertNotNull(event)
    }

    @Test
    fun `event is null if KMP beforeSend callback config returns null`() {
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = {
                null
            }
        }
        assertNull(event)
    }

    @Test
    fun `event is not null if KMP beforeSend callback config returns not null`() {
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event
            }
        }
        assertNotNull(event)
    }

    @Test
    fun `event logger is modified if KMP beforeSend callback config modifies it`() {
        val expected = "test"
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event.logger = expected
                event
            }
        }
        assertNotNull(event)
        assertEquals(expected, event.logger)
    }

    @Test
    fun `event level is modified if KMP beforeSend callback config modifies it`() {
        val expected = SentryLevel.DEBUG
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event.level = expected
                event
            }
        }
        assertNotNull(event)
        assertEquals(expected, event.level)
    }

    @Test
    fun `event message is modified if KMP beforeSend callback config modifies it`() {
        val expected = Message("test")
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event.message = expected
                event
            }
        }
        assertNotNull(event)
        assertEquals(expected, event.message)
    }

    @Test
    fun `event release is modified if KMP beforeSend callback config modifies it`() {
        val expected = "test"
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event.release = expected
                event
            }
        }
        assertNotNull(event)
        assertEquals(expected, event.release)
    }

    @Test
    fun `event environment is modified if KMP beforeSend callback config modifies it`() {
        val expected = "test"
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event.environment = expected
                event
            }
        }
        assertNotNull(event)
        assertEquals(expected, event.environment)
    }

    @Test
    fun `event serverName is modified if KMP beforeSend callback config modifies it`() {
        val expected = "test"
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event.serverName = expected
                event
            }
        }
        assertNotNull(event)
        assertEquals(expected, event.serverName)
    }

    @Test
    fun `event dist is modified if KMP beforeSend callback config modifies it`() {
        val expected = "test"
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event.dist = expected
                event
            }
        }
        assertNotNull(event)
        assertEquals(expected, event.dist)
    }

    @Test
    fun `event fingerprint is modified if KMP beforeSend callback config modifies it`() {
        val expected = mutableListOf("test")
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event.fingerprint = expected
                event
            }
        }
        assertNotNull(event)
        assertEquals(expected, event.fingerprint)
    }

    @Test
    fun `event tags are modified if KMP beforeSend callback config modifies it`() {
        val expected = mutableMapOf("test" to "test")
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event.tags = expected
                event
            }
        }
        assertNotNull(event)
        assertEquals(expected, event.tags)
    }

    @Test
    fun `event breadcrumbs are modified if KMP beforeSend callback config modifies it`() {
        val expected = mutableListOf(Breadcrumb.debug("test"))
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event.breadcrumbs = expected
                event
            }
        }
        assertNotNull(event)
        assertEquals(expected.first().type, event.breadcrumbs.first().type)
        assertEquals(expected.first().message, event.breadcrumbs.first().message)
        assertEquals(expected.first().level, event.breadcrumbs.first().level)
    }

    @Test
    fun `event logger is not modified if KMP beforeSend callback config is not modified`() {
        val originalEvent = sentryEventConfigurator.originalEvent
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
        }
        assertNotNull(event)
        assertEquals(originalEvent.logger, event.logger)
    }

    @Test
    fun `event level is not modified if KMP beforeSend callback config is not modified`() {
        val originalEvent = sentryEventConfigurator.originalEvent
        val event = sentryEventConfigurator.applyOptions()
        assertNotNull(event)
        assertEquals(originalEvent.level, event.level)
    }

    @Test
    fun `event message is not modified if KMP beforeSend callback config is not modified`() {
        val originalEvent = sentryEventConfigurator.originalEvent
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
        }
        assertNotNull(event)
        assertEquals(originalEvent.message, event.message)
    }

    @Test
    fun `event release is not modified if KMP beforeSend callback config is not modified`() {
        val originalEvent = sentryEventConfigurator.originalEvent
        val event = sentryEventConfigurator.applyOptions {
            it.release = "1.0.0"
            it.dsn = fakeDsn
        }
        assertNotNull(event)
        assertEquals(originalEvent.release, event.release)
    }

    @Test
    fun `event environment is not modified if KMP beforeSend callback config is not modified`() {
        val originalEvent = sentryEventConfigurator.originalEvent
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
        }
        assertNotNull(event)
        assertEquals(originalEvent.environment, event.environment)
    }

    @Test
    fun `event serverName is not modified if KMP beforeSend callback config is not modified`() {
        val originalEvent = sentryEventConfigurator.originalEvent
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
            it.beforeSend = { event ->
                event
            }
        }
        assertNotNull(event)
        assertEquals(originalEvent.serverName, event.serverName)
    }

    @Test
    fun `event dist is not modified if KMP beforeSend callback config is not modified`() {
        val originalEvent = sentryEventConfigurator.originalEvent
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
        }
        assertNotNull(event)
        assertEquals(originalEvent.dist, event.dist)
    }

    @Test
    fun `event fingerprint is not modified if KMP beforeSend callback config is not modified`() {
        val originalEvent = sentryEventConfigurator.originalEvent
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
        }
        assertNotNull(event)
        assertEquals(originalEvent.fingerprint, event.fingerprint)
    }

    @Test
    fun `event tags are not modified if KMP beforeSend callback config is not modified`() {
        val originalEvent = sentryEventConfigurator.originalEvent
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
        }
        assertNotNull(event)
        assertEquals(originalEvent.tags, event.tags)
    }

    @Test
    fun `event breadcrumbs are not modified if KMP beforeSend callback config is not modified`() {
        val originalEvent = sentryEventConfigurator.originalEvent
        val event = sentryEventConfigurator.applyOptions {
            it.dsn = fakeDsn
        }
        assertNotNull(event)
        assertEquals(originalEvent.breadcrumbs, event.breadcrumbs)
    }
}
