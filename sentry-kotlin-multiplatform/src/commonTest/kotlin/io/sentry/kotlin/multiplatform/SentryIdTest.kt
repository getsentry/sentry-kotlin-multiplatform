package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals

class SentryIdTest {
    @Test
    fun `SentryId with valid uuid string returns valid SentryId string`() {
        val uuidString = "ec81a720-b6f6-4efc-9d74-6627a09157c1"
        val sentryId = SentryId(uuidString)

        val expected = "ec81a720b6f64efc9d746627a09157c1"
        val actual = sentryId.toString()

        assertEquals(expected, actual)
        assertEquals(32, actual.length)
    }

    @Test
    fun `te`() {
        //SentryKMP.captureException(RuntimeException("test")
    }
}