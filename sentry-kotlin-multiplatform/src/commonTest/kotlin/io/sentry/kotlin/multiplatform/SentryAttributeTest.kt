package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** Tests for [SentryAttributeValue] factory methods and type hierarchy. */
class SentryAttributeTest {

    @Test
    fun `string factory creates StringAttribute with correct key and value`() {
        val attr = SentryAttributeValue.string("myKey", "myValue")

        assertIs<SentryAttributeValue.StringAttributeValue>(attr)
        assertEquals("myKey", attr.key)
        assertEquals("myValue", attr.value)
    }

    @Test
    fun `int factory creates IntAttribute with correct key and value`() {
        val attr = SentryAttributeValue.int("count", 42)

        assertIs<SentryAttributeValue.IntAttributeValue>(attr)
        assertEquals("count", attr.key)
        assertEquals(42, attr.value)
    }

    @Test
    fun `double factory creates DoubleAttribute with correct key and value`() {
        val attr = SentryAttributeValue.double("price", 19.99)

        assertIs<SentryAttributeValue.DoubleAttributeValue>(attr)
        assertEquals("price", attr.key)
        assertEquals(19.99, attr.value)
    }

    @Test
    fun `boolean factory creates BooleanAttribute with correct key and value`() {
        val attr = SentryAttributeValue.boolean("enabled", true)

        assertIs<SentryAttributeValue.BooleanAttributeValue>(attr)
        assertEquals("enabled", attr.key)
        assertEquals(true, attr.value)
    }
}

