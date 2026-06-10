package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Tests for [SentryAttributes] collection operations. */
class SentryAttributesTest {

    @Test
    fun `set and get string value`() {
        val attrs = SentryAttributes.empty()

        attrs["key"] = "value"

        assertEquals("value", attrs["key"]?.stringOrNull)
    }

    @Test
    fun `set and get int value stores as Long`() {
        val attrs = SentryAttributes.empty()

        attrs["count"] = 42

        assertEquals(42L, attrs["count"]?.longOrNull)
    }

    @Test
    fun `set and get long value`() {
        val attrs = SentryAttributes.empty()
        val longValue = 9223372036854775807L

        attrs["timestamp"] = longValue

        assertEquals(longValue, attrs["timestamp"]?.longOrNull)
    }

    @Test
    fun `set and get double value`() {
        val attrs = SentryAttributes.empty()

        attrs["price"] = 19.99

        assertEquals(19.99, attrs["price"]?.doubleOrNull)
    }

    @Test
    fun `set and get boolean value`() {
        val attrs = SentryAttributes.empty()

        attrs["enabled"] = true

        assertEquals(true, attrs["enabled"]?.booleanOrNull)
    }

    @Test
    fun `remove removes entry`() {
        val attrs = SentryAttributes.empty()
        attrs["key"] = "value"

        attrs.remove("key")

        assertNull(attrs["key"])
    }

    @Test
    fun `contains returns true for existing key`() {
        val attrs = SentryAttributes.empty()
        attrs["exists"] = "yes"

        assertTrue("exists" in attrs)
    }

    @Test
    fun `contains returns false for missing key`() {
        val attrs = SentryAttributes.empty()

        assertFalse("missing" in attrs)
    }

    @Test
    fun `typed getters return null for wrong type`() {
        val attrs = SentryAttributes.empty()
        attrs["key"] = "text"

        assertNull(attrs["key"]?.longOrNull)
        assertNull(attrs["key"]?.doubleOrNull)
        assertNull(attrs["key"]?.booleanOrNull)
    }

    @Test
    fun `typed getters return null for missing key`() {
        val attrs = SentryAttributes.empty()

        assertNull(attrs["missing"]?.stringOrNull)
        assertNull(attrs["missing"]?.longOrNull)
    }

    @Test
    fun `of from map creates SentryAttributes with entries`() {
        val map = mapOf(
            "name" to SentryAttributeValue.string("test"),
            "count" to SentryAttributeValue.long(5)
        )

        val attrs = SentryAttributes.of(map)

        assertEquals("test", attrs["name"]?.stringOrNull)
        assertEquals(5L, attrs["count"]?.longOrNull)
    }

    @Test
    fun `set replaces existing entry with same key`() {
        val attrs = SentryAttributes.empty()
        attrs["key"] = "old"
        attrs["key"] = "new"

        assertEquals("new", attrs["key"]?.stringOrNull)
    }

    @Test
    fun `Int and Long both stored as LongValue`() {
        val attrs = SentryAttributes.empty()
        attrs["fromInt"] = 42
        attrs["fromLong"] = 42L

        // Both should be stored as Long
        assertEquals(42L, attrs["fromInt"]?.longOrNull)
        assertEquals(42L, attrs["fromLong"]?.longOrNull)
    }

    @Test
    fun `forEach iterates over all entries`() {
        val attrs = SentryAttributes.empty()
        attrs["a"] = "1"
        attrs["b"] = 2

        val keys = mutableListOf<String>()
        attrs.forEach { (key, _) -> keys.add(key) }

        assertEquals(2, keys.size)
        assertTrue("a" in keys)
        assertTrue("b" in keys)
    }

    @Test
    fun `size returns correct count`() {
        val attrs = SentryAttributes.empty()
        attrs["a"] = "1"
        attrs["b"] = 2

        assertEquals(2, attrs.size)
    }

    @Test
    fun `isEmpty returns true for empty attributes`() {
        val attrs = SentryAttributes.empty()

        assertTrue(attrs.isEmpty())
    }

    @Test
    fun `isEmpty returns false for non-empty attributes`() {
        val attrs = SentryAttributes.empty()
        attrs["key"] = "value"

        assertFalse(attrs.isEmpty())
    }

    @Test
    fun `copy creates independent copy of attributes`() {
        val original = SentryAttributes.empty()
        original["key1"] = "value1"
        original["key2"] = 42L

        val copy = original.copy()

        assertEquals("value1", copy["key1"]?.stringOrNull)
        assertEquals(42L, copy["key2"]?.longOrNull)
    }

    @Test
    fun `copy is independent from original - modifying copy does not affect original`() {
        val original = SentryAttributes.empty()
        original["key1"] = "value1"

        val copy = original.copy()
        copy["key1"] = "modified"
        copy["key2"] = "new"

        assertEquals("value1", original["key1"]?.stringOrNull)
        assertNull(original["key2"])

        assertEquals("modified", copy["key1"]?.stringOrNull)
        assertEquals("new", copy["key2"]?.stringOrNull)
    }

    @Test
    fun `copy is independent from original - modifying original does not affect copy`() {
        val original = SentryAttributes.empty()
        original["key1"] = "value1"

        val copy = original.copy()
        original["key1"] = "modified"
        original["key2"] = "new"

        assertEquals("value1", copy["key1"]?.stringOrNull)
        assertNull(copy["key2"])

        assertEquals("modified", original["key1"]?.stringOrNull)
        assertEquals("new", original["key2"]?.stringOrNull)
    }

    @Test
    fun `copy of empty attributes creates empty copy`() {
        val original = SentryAttributes.empty()
        val copy = original.copy()

        assertTrue(copy.isEmpty())
        assertEquals(0, copy.size)
    }
}
