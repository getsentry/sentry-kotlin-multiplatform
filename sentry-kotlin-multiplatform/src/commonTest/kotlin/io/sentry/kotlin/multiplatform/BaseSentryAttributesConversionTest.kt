package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Base test class for SentryAttributes conversion tests.
 * Platform-specific tests should extend this class and provide
 * the conversion implementation.
 */
abstract class BaseSentryAttributesConversionTest {
    /**
     * Converts SentryAttributes to a platform-specific map for assertions.
     * Each platform provides its own implementation using the appropriate converter.
     */
    abstract fun SentryAttributes.toMap(): Map<String, Any?>

    @Test
    fun `empty attributes converts to empty map`() {
        val attrs = SentryAttributes.empty()

        val map = attrs.toMap()

        assertTrue(map.isEmpty())
    }

    @Test
    fun `string attribute converts correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["key"] = "value"

        val map = attrs.toMap()

        assertEquals("value", map["key"])
    }

    @Test
    fun `long attribute converts correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["count"] = 42L

        val map = attrs.toMap()

        assertEquals(42L, map["count"])
    }

    @Test
    fun `double attribute converts correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["ratio"] = 3.14

        val map = attrs.toMap()

        assertEquals(3.14, map["ratio"])
    }

    @Test
    fun `boolean attribute converts correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["enabled"] = true

        val map = attrs.toMap()

        assertEquals(true, map["enabled"])
    }

    @Test
    fun `multiple attributes convert correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["string"] = "text"
        attrs["number"] = 123L
        attrs["decimal"] = 1.5
        attrs["flag"] = false

        val map = attrs.toMap()

        assertEquals(4, map.size)
        assertEquals("text", map["string"])
        assertEquals(123L, map["number"])
        assertEquals(1.5, map["decimal"])
        assertEquals(false, map["flag"])
    }

    @Test
    fun `int converts to long`() {
        val attrs = SentryAttributes.empty()
        attrs["int_value"] = 42

        val map = attrs.toMap()

        assertEquals(42L, map["int_value"])
    }

    @Test
    fun `unicode values convert correctly`() {
        val attrs = SentryAttributes.empty()
        attrs["emoji"] = "🚀"
        attrs["chinese"] = "中文"

        val map = attrs.toMap()

        assertEquals("🚀", map["emoji"])
        assertEquals("中文", map["chinese"])
    }
}
