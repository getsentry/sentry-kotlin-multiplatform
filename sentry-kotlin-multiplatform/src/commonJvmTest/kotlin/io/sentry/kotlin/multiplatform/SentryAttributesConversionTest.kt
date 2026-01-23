package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.log.toJvmSentryAttributes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Tests for SentryAttributes to JVM conversion. */
class SentryAttributesConversionTest {
    @Test
    fun `empty attributes converts to empty JVM attributes`() {
        val kmpAttrs = SentryAttributes.empty()

        val jvmAttrs = kmpAttrs.toJvmSentryAttributes()

        assertTrue(jvmAttrs.attributes.isEmpty())
    }

    @Test
    fun `string attribute converts correctly`() {
        val kmpAttrs = SentryAttributes.empty()
        kmpAttrs["key"] = "value"

        val jvmAttrs = kmpAttrs.toJvmSentryAttributes()

        assertEquals("value", jvmAttrs.attributes["key"])
    }

    @Test
    fun `long attribute converts correctly`() {
        val kmpAttrs = SentryAttributes.empty()
        kmpAttrs["count"] = 42L

        val jvmAttrs = kmpAttrs.toJvmSentryAttributes()

        assertEquals(42L, jvmAttrs.attributes["count"])
    }

    @Test
    fun `double attribute converts correctly`() {
        val kmpAttrs = SentryAttributes.empty()
        kmpAttrs["ratio"] = 3.14

        val jvmAttrs = kmpAttrs.toJvmSentryAttributes()

        assertEquals(3.14, jvmAttrs.attributes["ratio"])
    }

    @Test
    fun `boolean attribute converts correctly`() {
        val kmpAttrs = SentryAttributes.empty()
        kmpAttrs["enabled"] = true

        val jvmAttrs = kmpAttrs.toJvmSentryAttributes()

        assertEquals(true, jvmAttrs.attributes["enabled"])
    }

    @Test
    fun `multiple attributes convert correctly`() {
        val kmpAttrs = SentryAttributes.empty()
        kmpAttrs["string"] = "text"
        kmpAttrs["number"] = 123L
        kmpAttrs["decimal"] = 1.5
        kmpAttrs["flag"] = false

        val jvmAttrs = kmpAttrs.toJvmSentryAttributes()

        assertEquals(4, jvmAttrs.attributes.size)
        assertEquals("text", jvmAttrs.attributes["string"])
        assertEquals(123L, jvmAttrs.attributes["number"])
        assertEquals(1.5, jvmAttrs.attributes["decimal"])
        assertEquals(false, jvmAttrs.attributes["flag"])
    }

    @Test
    fun `int converts to long`() {
        val kmpAttrs = SentryAttributes.empty()
        kmpAttrs["int_value"] = 42 // Int gets stored as Long

        val jvmAttrs = kmpAttrs.toJvmSentryAttributes()

        assertEquals(42L, jvmAttrs.attributes["int_value"])
    }
}
