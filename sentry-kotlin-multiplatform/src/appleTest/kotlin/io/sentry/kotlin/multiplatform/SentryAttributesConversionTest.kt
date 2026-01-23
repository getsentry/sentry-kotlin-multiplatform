package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.log.toCocoaMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Tests for SentryAttributes to Cocoa map conversion. */
class SentryAttributesConversionTest {
    @Test
    fun `empty attributes converts to empty map`() {
        val attrs = SentryAttributes.empty()

        val map = attrs.toCocoaMap()

        assertTrue(map.isEmpty())
    }

    @Test
    fun `string attribute converts correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["key"] = "value"

        val map = attrs.toCocoaMap()

        assertEquals("value", map["key"])
    }

    @Test
    fun `long attribute converts correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["count"] = 42L

        val map = attrs.toCocoaMap()

        assertEquals(42L, map["count"])
    }

    @Test
    fun `double attribute converts correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["ratio"] = 3.14

        val map = attrs.toCocoaMap()

        assertEquals(3.14, map["ratio"])
    }

    @Test
    fun `boolean attribute converts correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["enabled"] = true

        val map = attrs.toCocoaMap()

        assertEquals(true, map["enabled"])
    }

    @Test
    fun `multiple attributes convert correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["string"] = "text"
        attrs["number"] = 123L
        attrs["decimal"] = 1.5
        attrs["flag"] = false

        val map = attrs.toCocoaMap()

        assertEquals(4, map.size)
        assertEquals("text", map["string"])
        assertEquals(123L, map["number"])
        assertEquals(1.5, map["decimal"])
        assertEquals(false, map["flag"])
    }

    @Test
    fun `int converts to long`() {
        val attrs = SentryAttributes.empty()
        attrs["int_value"] = 42  // Int gets stored as Long

        val map = attrs.toCocoaMap()

        assertEquals(42L, map["int_value"])
    }

    @Test
    fun `unicode values convert correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["emoji"] = "🚀"
        attrs["chinese"] = "中文"

        val map = attrs.toCocoaMap()

        assertEquals("🚀", map["emoji"])
        assertEquals("中文", map["chinese"])
    }
}
