package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.fakes.createFakeTransactionContext
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SentryTransactionTest : BaseSentryTest() {
    class Fixture {
        val name = "test name"
        val operation = "test description"

        internal fun getSut(
            name: String = this@Fixture.name,
            operation: String = this@Fixture.operation,
            bindToScope: Boolean
        ): Span {
            return Sentry.startTransaction(name, operation, bindToScope)
        }

        internal fun getSut(
            name: String = this@Fixture.name,
            operation: String = this@Fixture.operation
        ): Span {
            return Sentry.startTransaction(name, operation)
        }

        internal fun getSutWithTransactionContext(
            transactionContext: TransactionContext,
            customSamplingContext: CustomSamplingContext
        ): Span {
            return Sentry.startTransaction(transactionContext, customSamplingContext)
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
        var actualName = ""
        Sentry.init {
            it.dsn = fakeDsn
            it.tracesSampler = { context ->
                print("hello")
                actualName = context.transactionContext.name
                null
            }
        }

        // WHEN
        val transaction = fixture.getSut(fixture.name, fixture.operation)
        transaction.finish()

        // THEN
        assertEquals(fixture.name, actualName)
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
    fun `GIVEN Sentry init AND parent + child spans with operation WHEN both started and finished THEN child parentSpanId equals spanId of parent`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }

        // WHEN
        val transaction = fixture.getSut()
        val child = transaction.startChild("child", "description")
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
        val transaction =
            fixture.getSut(bindToScope = true)
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

    @Test
    fun `GIVEN Sentry init WHEN transaction finishes with status OK THEN span status should be OK`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }

        // WHEN
        val transaction = fixture.getSut()
        transaction.finish(SpanStatus.OK)

        // THEN
        assertEquals(SpanStatus.OK, transaction.status)
    }

    @Test
    fun `GIVEN Sentry init WHEN overriding status on transaction THEN status returns correct value`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }

        // WHEN
        val transaction = fixture.getSut()
        transaction.status = SpanStatus.ABORTED

        // THEN
        assertEquals(SpanStatus.ABORTED, transaction.status)
    }

    @Test
    fun `GIVEN Sentry init WHEN overriding operation on transaction THEN operation returns correct value`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }
        val expectedOperation = "custom operation"

        // WHEN
        val transaction = fixture.getSut()
        transaction.operation = expectedOperation

        // THEN
        assertEquals(expectedOperation, transaction.operation)
    }

    @Test
    fun `GIVEN Sentry init WHEN setData is used on transaction THEN getData will return correct value`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }
        val expectedKey = "key"
        val expectedValue = "value"

        // WHEN
        val transaction = fixture.getSut()
        transaction.setData(expectedKey, expectedValue)

        // THEN
        assertEquals(expectedValue, transaction.getData(expectedKey))
    }

    @Test
    fun `GIVEN Sentry init WHEN setTag is used on transaction THEN getTag will return correct value`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }
        val expectedKey = "key"
        val expectedValue = "value"

        // WHEN
        val transaction = fixture.getSut()
        transaction.setTag(expectedKey, expectedValue)

        // THEN
        assertEquals(expectedValue, transaction.getTag(expectedKey))
    }

    @Test
    fun `GIVEN Sentry init WHEN transaction starts with operation THEN span should return correct operation`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }
        val expectedOperation = "custom operation"

        // WHEN
        val transaction = fixture.getSut(operation = expectedOperation)

        // THEN
        assertEquals(expectedOperation, transaction.operation)
    }

    @Test
    fun `GIVEN Sentry init WHEN transaction is given description THEN span should return correct description`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }
        val expectedDescription = "custom description"

        // WHEN
        val transaction = fixture.getSut()
        transaction.description = expectedDescription

        // THEN
        assertEquals(expectedDescription, transaction.description)
    }

    @Test
    fun `GIVEN Sentry init WHEN transaction calls finish THEN span should return isFinish true`() {
        // GIVEN
        Sentry.init {
            it.dsn = fakeDsn
        }

        // WHEN
        val transaction = fixture.getSut()
        transaction.finish()

        // THEN
        assertEquals(true, transaction.isFinished)
    }

    @Test
    fun `GIVEN Sentry init WHEN startTransaction is given transactionContext with sampled not null THEN tracesSampler is ignored`() {
        // GIVEN
        var expectedTransactionContext: TransactionContext? = null
        Sentry.init {
            it.dsn = fakeDsn
            it.tracesSampler = { _ ->
                expectedTransactionContext = createFakeTransactionContext(operation = "different")
                null
            }
        }

        // WHEN
        fixture.getSutWithTransactionContext(
            transactionContext = createFakeTransactionContext(sampled = true),
            customSamplingContext = null
        )

        // THEN
        assertNull(expectedTransactionContext)
    }

    @Test
    fun `GIVEN Sentry init WHEN startTransaction is given transactionContext with sampled null THEN tracesSampler is called`() {
        // GIVEN
        var actualTransactionContext: TransactionContext? = null
        Sentry.init {
            it.dsn = fakeDsn
            it.tracesSampler = { context ->
                actualTransactionContext = context.transactionContext
                null
            }
        }

        // WHEN
        val expectedTransactionContext = createFakeTransactionContext(sampled = null)
        fixture.getSutWithTransactionContext(
            transactionContext = expectedTransactionContext,
            customSamplingContext = null
        )

        // THEN
        assertEquals(expectedTransactionContext.name, actualTransactionContext?.name)
        assertEquals(expectedTransactionContext.description, actualTransactionContext?.description)
        assertEquals(expectedTransactionContext.sampled, actualTransactionContext?.sampled)
        assertEquals(expectedTransactionContext.name, actualTransactionContext?.name)
        assertEquals(
            expectedTransactionContext.parentSampled,
            actualTransactionContext?.parentSampled
        )
    }
}
