package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** Tests for [SentryAttributeValue] factory methods and type hierarchy. */
class SentryAttributeValueTest {

    @Test
    fun `string factory creates StringValue with correct value`() {
        val value = SentryAttributeValue.string("myValue")

        assertIs<SentryAttributeValue.StringValue>(value)
        assertEquals("myValue", value.value)
    }

    @Test
    fun `long factory creates LongValue with correct value`() {
        val value = SentryAttributeValue.long(9223372036854775807L)

        assertIs<SentryAttributeValue.LongValue>(value)
        assertEquals(9223372036854775807L, value.value)
    }

    @Test
    fun `double factory creates DoubleValue with correct value`() {
        val value = SentryAttributeValue.double(19.99)

        assertIs<SentryAttributeValue.DoubleValue>(value)
        assertEquals(19.99, value.value)
    }

    @Test
    fun `boolean factory creates BooleanValue with correct value`() {
        val value = SentryAttributeValue.boolean(true)

        assertIs<SentryAttributeValue.BooleanValue>(value)
        assertEquals(true, value.value)
    }
}
