package io.sentry.kotlin.multiplatform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SpanStatusTest {
    @Test
    fun `converts http status code to SpanStatus when code matches range`() {
        assertEquals(SpanStatus2.OK, SpanStatus2.fromHttpStatusCode(202))
    }

    @Test
    fun `converts http status code to SpanStatus equals specific code`() {
        assertEquals(SpanStatus2.DEADLINE_EXCEEDED, SpanStatus2.fromHttpStatusCode(504))
    }

    @Test
    fun `converts http status code to first SpanStatus matching specific code`() {
        assertEquals(SpanStatus2.INTERNAL_ERROR, SpanStatus2.fromHttpStatusCode(500))
    }

    @Test
    fun `returns null when no SpanStatus matches specific code`() {
        assertNull(SpanStatus2.fromHttpStatusCode(302))
    }

    @Test
    fun `returns default value when no SpanStatus matches specific code`() {
        assertEquals(SpanStatus2.UNKNOWN_ERROR, SpanStatus2.fromHttpStatusCode(302, SpanStatus2.UNKNOWN_ERROR))
    }

    @Test
    fun `returns default value when http code is null`() {
        assertEquals(SpanStatus2.UNKNOWN_ERROR, SpanStatus2.fromHttpStatusCode(null, SpanStatus2.UNKNOWN_ERROR))
    }
}

