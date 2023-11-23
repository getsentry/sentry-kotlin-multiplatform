package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SpanTest : BaseSentryTest() {
    class Fixture {
        private val operation = "test"
        private val description = "test description"

        internal fun getSut(bindToScope: Boolean = false): Span {
            return Sentry.startTransaction(operation, description, bindToScope)
        }
    }

    private lateinit var fixture: Fixture

    @BeforeTest
    fun setup() {
        fixture = Fixture()
    }

    @Test
    fun `GIVEN Sentry init AND tracesSampler set WHEN transaction finishes THEN receive correct name`() {
        // GIVEN
        val expectedName = "test"
        var actualName = ""
        Sentry.init {
            it.dsn = fakeDsn
            it.tracesSampler = { context ->
                actualName = context.transactionContext.name
                null
            }
        }

        // WHEN
        val transaction = fixture.getSut()
        transaction.finish()

        // THEN
        assertEquals(expectedName, actualName)
    }

    @Test
    fun `GIVEN Sentry init AND tracesSampler set WHEN transaction started and finishes THEN receive correct spanId`() {
        // GIVEN
        var actualSpanId = ""
        Sentry.init {
            it.dsn = fakeDsn
            it.tracesSampler = { context ->
                actualSpanId = context.transactionContext.spanId.toString()
                null
            }
        }

        // WHEN
        val transaction = fixture.getSut()
        transaction.finish()

        // THEN
        assertEquals(transaction.spanId.toString(), actualSpanId)
    }

    @Test
    fun `GIVEN Sentry init AND parent + child spans WHEN both started and finished THEN child parentSpanId equals spanId of parent`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }

        // WHEN
        val transaction = fixture.getSut()
        val child = transaction.startChild("child")
        child.finish()
        transaction.finish()

        // THEN
        assertEquals(child.parentSpanId?.toString(), transaction.spanId.toString())
    }

    @Test
    fun `GIVEN Sentry init WHEN transaction with bindToScope enabled started AND getSpan called THEN should return the correct transaction`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }

        // WHEN
        val transaction = fixture.getSut(bindToScope = true)
        val activeTransactionSpanId = Sentry.getSpan()?.spanId

        // THEN
        assertEquals(
            activeTransactionSpanId,
            transaction.spanId,
            "activeTransactionSpanId should be equal to transaction.spanId"
        )
        transaction.finish()
    }

    @Test
    fun `GIVEN Sentry init WHEN transaction with bindToScope disabled started AND getSpan called THEN getSpan should be null`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }

        // WHEN
        val transaction = fixture.getSut()
        val activeTransactionSpanId = Sentry.getSpan()?.spanId

        // THEN
        assertNull(activeTransactionSpanId)
        transaction.finish()
    }

    @Test
    fun `GIVEN Sentry init WHEN transaction with bindToScope enabled starts and finishes THEN calling getSpan after should be null`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }

        // WHEN
        val transaction = fixture.getSut(bindToScope = true)
        transaction.finish()

        // THEN
        assertNull(Sentry.getSpan()?.spanId)
    }
}
