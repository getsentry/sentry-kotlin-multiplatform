package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId
import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TransactionContextTest {
    class Fixture {
        val name = "test name"
        val operation = "test operation"
        val sampled = false

        fun getSut(
            name: String = this.name,
            operation: String = this.operation,
            sampled: Boolean
        ): TransactionContext {
            return TransactionContext(operation = operation, name = name, sampled = sampled)
        }

        fun getSut(
            operation: String
        ): TransactionContext {
            return TransactionContext(operation = operation)
        }

        fun getSut(
            name: String = this.name,
            operation: String = this.operation
        ): TransactionContext {
            return TransactionContext(operation = operation, name = name)
        }
    }

    private lateinit var fixture: Fixture

    @BeforeTest
    fun setup() {
        fixture = Fixture()
    }

    @Test
    fun `GIVEN name WHEN transactionContext created THEN receive correct name`() {
        // GIVEN
        val expectedName = "other name"

        // WHEN
        val transactionContext = fixture.getSut(name = expectedName)
        val actualName = transactionContext.name

        // THEN
        assertEquals(expectedName, actualName)
    }

    @Test
    fun `GIVEN operation WHEN transactionContext created THEN receive correct operation`() {
        // GIVEN
        val expectedOperation = "other operation"

        // WHEN
        val transactionContext = fixture.getSut(
            operation = expectedOperation
        )
        val actualOperation = transactionContext.operation

        // THEN
        assertEquals(expectedOperation, actualOperation)
    }

    @Test
    fun `GIVEN sampled WHEN transactionContext created THEN receive correct sampled`() {
        // GIVEN
        val expectedSampled = true

        // WHEN
        val transactionContext = fixture.getSut(
            sampled = expectedSampled
        )
        val actualSampled = transactionContext.sampled

        // THEN
        assertEquals(expectedSampled, actualSampled)
    }

    @Test
    fun `WHEN transactionContext created THEN receive default parentSampled`() {
        // WHEN
        val transactionContext = fixture.getSut()
        val actualParentSampled = transactionContext.parentSampled

        // THEN
        assertNull(actualParentSampled)
    }

    @Test
    fun `WHEN transactionContext created THEN receive default traceId`() {
        // WHEN
        val transactionContext = fixture.getSut()
        val actualTraceId = transactionContext.traceId

        // THEN
        assertEquals(SentryId.EMPTY_ID, actualTraceId)
    }

    @Test
    fun `WHEN transactionContext created THEN receive default spanId`() {
        // WHEN
        val transactionContext = fixture.getSut()
        val actualSpanId = transactionContext.spanId

        // THEN
        assertEquals(SpanId.EMPTY_ID, actualSpanId)
    }

    @Test
    fun `WHEN transactionContext created THEN receive default parentSpanId`() {
        // WHEN
        val transactionContext = fixture.getSut()
        val actualParentSpanId = transactionContext.parentSpanId

        // THEN
        assertEquals(null, actualParentSpanId)
    }

    @Test
    fun `WHEN transactionContext created THEN receive default description`() {
        // WHEN
        val transactionContext = fixture.getSut()
        val actualDescription = transactionContext.description

        // THEN
        assertEquals(null, actualDescription)
    }

    @Test
    fun `WHEN transactionContext created THEN receive default transactionNameSource`() {
        // WHEN
        val transactionContext = fixture.getSut()
        val actualTransactionNameSource = transactionContext.transactionNameSource

        // THEN
        assertEquals(TransactionNameSource.CUSTOM, actualTransactionNameSource)
    }

    @Test
    fun `WHEN transactionContext with name and operation THEN sampling decision and parent sampling is not set`() {
        // WHEN
        val transactionContext = fixture.getSut()

        // THEN
        assertNull(transactionContext.sampled)
        assertNull(transactionContext.parentSampled)
    }

    @Test
    fun `GIVEN operation WHEN transactionContext created only with operation THEN receive correct operation`() {
        // GIVEN
        val expectedOperation = "other operation"

        // WHEN
        val transactionContext = fixture.getSut(
            operation = expectedOperation
        )
        val actualOperation = transactionContext.operation

        // THEN
        assertEquals(expectedOperation, actualOperation)
    }

    @Test
    fun `GIVEN TransactionContext instance WHEN checking the type THEN should be TransactionContextImpl`() {
        // GIVEN
        val transactionContext = fixture.getSut()

        // WHEN
        val actual = transactionContext is TransactionContextImpl

        // THEN
        assertTrue(actual)
    }
}
