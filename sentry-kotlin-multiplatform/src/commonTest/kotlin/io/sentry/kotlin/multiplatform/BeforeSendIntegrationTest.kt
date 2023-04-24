package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BeforeSendIntegrationTest {
    private val sentryOptionsConfigurator = SentryOptionsConfigurator()

    @Test
    fun `event is not null if KMP beforeSend option is null`() {
        val event = sentryOptionsConfigurator.applyOptions()
        assertNotNull(event)
    }

    @Test
    fun `event is null if KMP beforeSend callback config returns null`() {
        val event = sentryOptionsConfigurator.applyOptions {
            it.beforeSend = {
                null
            }
        }
        assertNull(event)
    }

    @Test
    fun `event is not null if KMP beforeSend callback config returns not null`() {
        val event = sentryOptionsConfigurator.applyOptions {
            it.beforeSend = { event ->
                event
            }
        }
        assertNotNull(event)
    }

    @Test
    fun `event is modified if KMP beforeSend callback config modifies it`() {
        val expected = "test"
        val event = sentryOptionsConfigurator.applyOptions {
            it.beforeSend = { event ->
                event.logger = expected
                event
            }
        }
        assertNotNull(event)
        assertEquals(expected, event.logger)
    }
}
