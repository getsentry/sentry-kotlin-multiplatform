package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SpanStatusTest {
    @Test
    fun `GIVEN http status code in a matching range WHEN converting to SpanStatus THEN returns corresponding SpanStatus`() {
        // GIVEN
        val httpStatusCode = 202

        // WHEN
        val convertedStatus = SpanStatus.fromHttpStatusCode(httpStatusCode)

        // THEN
        assertEquals(SpanStatus.OK, convertedStatus)
    }

    @Test
    fun `GIVEN specific http status code WHEN converting to SpanStatus THEN returns corresponding SpanStatus`() {
        // GIVEN
        val httpStatusCode = 504

        // WHEN
        val convertedStatus = SpanStatus.fromHttpStatusCode(httpStatusCode)

        // THEN
        assertEquals(SpanStatus.DEADLINE_EXCEEDED, convertedStatus)
    }

    @Test
    fun `GIVEN http status code matching multiple statuses WHEN converting to SpanStatus THEN returns the first matching SpanStatus`() {
        // GIVEN
        val httpStatusCode = 500

        // WHEN
        val convertedStatus = SpanStatus.fromHttpStatusCode(httpStatusCode)

        // THEN
        assertEquals(SpanStatus.INTERNAL_ERROR, convertedStatus)
    }

    @Test
    fun `GIVEN http status code with no matching SpanStatus WHEN converting to SpanStatus THEN returns null`() {
        // GIVEN
        val httpStatusCode = 302

        // WHEN
        val convertedStatus = SpanStatus.fromHttpStatusCode(httpStatusCode)

        // THEN
        assertNull(convertedStatus)
    }

    @Test
    fun `GIVEN http status code with no matching SpanStatus AND default value provided WHEN converting to SpanStatus THEN returns the default value`() {
        // GIVEN
        val httpStatusCode = 302
        val defaultValue = SpanStatus.UNKNOWN_ERROR

        // WHEN
        val convertedStatus = SpanStatus.fromHttpStatusCode(httpStatusCode, defaultValue)

        // THEN
        assertEquals(defaultValue, convertedStatus)
    }

    @Test
    fun `GIVEN null http status code AND default value provided WHEN converting to SpanStatus THEN returns the default value`() {
        // GIVEN
        val httpStatusCode: Int? = null
        val defaultValue = SpanStatus.UNKNOWN_ERROR

        // WHEN
        val convertedStatus = SpanStatus.fromHttpStatusCode(httpStatusCode, defaultValue)

        // THEN
        assertEquals(defaultValue, convertedStatus)
    }
}
