package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Tests for [SentryAttributes] collection operations. */
class SentryAttributesTest {

    @Test
    fun `setAttribute adds entry and get retrieves it`() {
        val attrs = SentryAttributes()
        val attr = SentryAttributeValue.string("key", "value")

        attrs.setAttribute(attr)

        assertEquals(attr, attrs["key"])
    }

    @Test
    fun `removeAttribute removes entry`() {
        val attrs = SentryAttributes()
        attrs.setAttribute(SentryAttributeValue.string("key", "value"))

        attrs.removeAttribute("key")

        assertNull(attrs["key"])
    }

    @Test
    fun `setAttributes from list adds all entries`() {
        val attrs = SentryAttributes()
        val list = listOf(
            SentryAttributeValue.string("a", "1"),
            SentryAttributeValue.int("b", 2)
        )

        attrs.setAttributes(list)

        assertEquals("1", attrs.getString("a"))
        assertEquals(2, attrs.getInt("b"))
    }

    @Test
    fun `setAttributes from map adds all entries`() {
        val attrs = SentryAttributes()
        val map = mapOf(
            "x" to SentryAttributeValue.double("x", 1.5),
            "y" to SentryAttributeValue.boolean("y", false)
        )

        attrs.setAttributes(map)

        assertEquals(1.5, attrs.getDouble("x"))
        assertEquals(false, attrs.getBoolean("y"))
    }

    @Test
    fun `contains returns true for existing key`() {
        val attrs = SentryAttributes()
        attrs.setAttribute(SentryAttributeValue.string("exists", "yes"))

        assertTrue("exists" in attrs)
    }

    @Test
    fun `contains returns false for missing key`() {
        val attrs = SentryAttributes()

        assertFalse("missing" in attrs)
    }

    @Test
    fun `typed getters return null for wrong type`() {
        val attrs = SentryAttributes()
        attrs.setAttribute(SentryAttributeValue.string("key", "text"))

        assertNull(attrs.getInt("key"))
        assertNull(attrs.getDouble("key"))
        assertNull(attrs.getBoolean("key"))
    }

    @Test
    fun `typed getters return null for missing key`() {
        val attrs = SentryAttributes()

        assertNull(attrs.getString("missing"))
        assertNull(attrs.getInt("missing"))
    }

    @Test
    fun `of from list creates SentryAttributes with entries`() {
        val list = listOf(
            SentryAttributeValue.string("name", "test"),
            SentryAttributeValue.int("count", 5)
        )

        val attrs = SentryAttributes.of(list)

        assertEquals("test", attrs.getString("name"))
        assertEquals(5, attrs.getInt("count"))
    }

    @Test
    fun `of from map creates SentryAttributes with entries`() {
        val map = mapOf(
            "enabled" to SentryAttributeValue.boolean("enabled", true)
        )

        val attrs = SentryAttributes.of(map)

        assertEquals(true, attrs.getBoolean("enabled"))
    }

    @Test
    fun `setAttribute replaces existing entry with same key`() {
        val attrs = SentryAttributes()
        attrs.setAttribute(SentryAttributeValue.string("key", "old"))
        attrs.setAttribute(SentryAttributeValue.string("key", "new"))

        assertEquals("new", attrs.getString("key"))
    }
}

