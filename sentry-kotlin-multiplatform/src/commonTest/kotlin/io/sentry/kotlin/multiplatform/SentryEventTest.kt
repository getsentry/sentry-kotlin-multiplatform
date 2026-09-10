package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
        assertEquals(mutableListOf(breadcrumb), event.breadcrumbs)
    }

    @Test
    fun `addBreadcrumb should add a new breadcrumb with a message`() {
        val event = SentryEvent()
        event.addBreadcrumb("test")
        assertEquals(1, event.breadcrumbs.size)
        assertEquals("test", event.breadcrumbs[0].message)
    }

    @Test
    fun `message should be set`() {
        val event = SentryEvent()
        event.message = Message("test")
        assertEquals("test", event.message?.message)
    }

    @Test
    fun `contexts should contain value if not empty`() {
        val event = SentryEvent()
        event.contexts = mutableMapOf("key" to "value")
        assertEquals("value", event.contexts["key"])
    }

    @Test
    fun `breadcrumbs should contain value if not empty`() {
        val event = SentryEvent()
        val breadcrumb = Breadcrumb(message = "test")
        event.breadcrumbs = mutableListOf(breadcrumb)
        assertEquals(mutableListOf(breadcrumb), event.breadcrumbs)
    }

    @Test
    fun `tags should contain value if not empty`() {
        val event = SentryEvent()
        event.tags = mutableMapOf("key" to "value")
        assertEquals("value", event.tags["key"])
    }

    @Test
    fun `fingerprint should be empty by default`() {
        val event = SentryEvent()
        assertTrue(event.fingerprint.isEmpty())
    }

    @Test
    fun `fingerprint should contain value if not empty`() {
        val event = SentryEvent()
        event.fingerprint = mutableListOf("error", "exception")
        assertEquals(mutableListOf("error", "exception"), event.fingerprint)
    }

    @Test
    fun `exceptions should be empty by default`() {
        val event = SentryEvent()
        assertTrue(event.exceptions.isEmpty())
    }

    @Test
    fun `exceptions should contain value if not empty`() {
        val event = SentryEvent()
        val exception1 = SentryException(type = "NullPointerException")
        val exception2 = SentryException(type = "IllegalArgumentException")
        event.exceptions = mutableListOf(exception1, exception2)
        assertEquals(mutableListOf(exception1, exception2), event.exceptions)
    }
}
