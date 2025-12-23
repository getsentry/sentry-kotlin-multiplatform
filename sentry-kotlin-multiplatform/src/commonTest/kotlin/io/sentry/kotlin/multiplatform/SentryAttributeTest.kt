package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** Tests for [SentryAttribute] factory methods and type hierarchy. */
class SentryAttributeTest {

    @Test
    fun `string factory creates StringAttribute with correct key and value`() {
        val attr = SentryAttribute.string("myKey", "myValue")

        assertIs<SentryAttribute.StringAttribute>(attr)
        assertEquals("myKey", attr.key)
        assertEquals("myValue", attr.value)
    }

    @Test
    fun `int factory creates IntAttribute with correct key and value`() {
        val attr = SentryAttribute.int("count", 42)

        assertIs<SentryAttribute.IntAttribute>(attr)
        assertEquals("count", attr.key)
        assertEquals(42, attr.value)
    }

    @Test
    fun `double factory creates DoubleAttribute with correct key and value`() {
        val attr = SentryAttribute.double("price", 19.99)

        assertIs<SentryAttribute.DoubleAttribute>(attr)
        assertEquals("price", attr.key)
        assertEquals(19.99, attr.value)
    }

    @Test
    fun `boolean factory creates BooleanAttribute with correct key and value`() {
        val attr = SentryAttribute.boolean("enabled", true)

        assertIs<SentryAttribute.BooleanAttribute>(attr)
        assertEquals("enabled", attr.key)
        assertEquals(true, attr.value)
    }
}

