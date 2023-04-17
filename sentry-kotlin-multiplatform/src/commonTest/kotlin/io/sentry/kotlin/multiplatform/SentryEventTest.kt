package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SentryEventTest {

    @Test
    fun `setTag should add a new tag`() {
        val event = SentryEvent()
        event.setTag("key", "value")
        assertEquals("value", event.getTag("key"))
    }

    @Test
    fun `removeTag should remove an existing tag`() {
        val event = SentryEvent()
        event.setTag("key", "value")
        event.removeTag("key")
        assertNull(event.getTag("key"))
    }

    @Test
    fun `addBreadcrumb should add a new breadcrumb`() {
        val event = SentryEvent()
        val breadcrumb = Breadcrumb(message = "test")
        event.addBreadcrumb(breadcrumb)
        assertEquals(listOf(breadcrumb), event.breadcrumbs)
    }

    @Test
    fun `addBreadcrumb should add a new breadcrumb with a message`() {
        val event = SentryEvent()
        event.addBreadcrumb("test")
        assertEquals(1, event.breadcrumbs?.size)
        assertEquals("test", event.breadcrumbs?.get(0)?.message)
    }

    @Test
    fun `message should be set`() {
        val event = SentryEvent()
        event.message = Message("test")
        assertEquals("test", event.message?.message)
    }

    @Test
    fun `contexts should contain value if not null`() {
        val event = SentryEvent()
        event.mutableContexts = mutableMapOf("key" to "value")
        assertEquals("value", event.contexts?.get("key"))
    }

    @Test
    fun `breadcrumbs should contain value if not null`() {
        val event = SentryEvent()
        val breadcrumb = Breadcrumb(message = "test")
        event.mutableBreadcrumbs = mutableListOf(breadcrumb)
        assertEquals(listOf(breadcrumb), event.breadcrumbs)
    }

    @Test
    fun `tags should contain value if not null`() {
        val event = SentryEvent()
        event.mutableTags = mutableMapOf("key" to "value")
        assertEquals("value", event.tags?.get("key"))
    }

    @Test
    fun `fingerprint should be null by default`() {
        val event = SentryEvent()
        assertNull(event.fingerprint)
    }

    @Test
    fun `fingerprint should contain value if not null`() {
        val event = SentryEvent()
        event.fingerprint = listOf("error", "exception")
        assertEquals(listOf("error", "exception"), event.fingerprint)
    }

    @Test
    fun `exceptions should be null by default`() {
        val event = SentryEvent()
        assertNull(event.exceptions)
    }

    @Test
    fun `exceptions should contain value if not null`() {
        val event = SentryEvent()
        val exception1 = SentryException(type = "NullPointerException")
        val exception2 = SentryException(type = "IllegalArgumentException")
        event.exceptions = listOf(exception1, exception2)
        assertEquals(listOf(exception1, exception2), event.exceptions)
    }
}
