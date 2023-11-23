package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId
import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource
import io.sentry.kotlin.multiplatform.utils.createFakeTransactionContext
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FakeTransactionContext(
    override val operation: String,
    override val traceId: SentryId,
    override val spanId: SpanId,
    override val parentSpanId: SpanId?,
    override val description: String?,
    override val sampled: Boolean,
    override val name: String,
    override val transactionNameSource: TransactionNameSource,
    override val parentSampled: Boolean
) : TransactionContext

class TracesSamplerRateTest {
    private lateinit var mockTransactionContext: TransactionContext

    @BeforeTest
    fun setup() {
        mockTransactionContext = createFakeTransactionContext()
    }

    @Test
    fun `tracesSampler can return null sample rate`() {
        val options = SentryOptions()
        options.tracesSampler = {
            null
        }
        val samplingContext = SamplingContext(
            transactionContext = mockTransactionContext,
        )
        val sampleRate = options.tracesSampler?.invoke(samplingContext)
        assertEquals(null, sampleRate)
    }

    @Test
    fun `tracesSampler can return sample rate`() {
        val options = SentryOptions()
        options.tracesSampler = {
            0.5
        }
        val samplingContext = SamplingContext(
            transactionContext = mockTransactionContext,
        )
        val sampleRate = options.tracesSampler?.invoke(samplingContext)
        assertEquals(0.5, sampleRate)
    }

    @Test
    fun `tracesSampler can return different sample rate`() {
        val options = SentryOptions()
        options.tracesSampler = {
            if (it.transactionContext.name == "test") {
                0.5
            } else {
                0.1
            }
        }

        // Assert that the sample rate is 0.5
        val samplingContext = SamplingContext(
            transactionContext = mockTransactionContext,
        )
        val sampleRate = options.tracesSampler?.invoke(samplingContext)
        assertEquals(0.5, sampleRate)

        // Assert that the sample rate is 0.1
        val differentSamplingContext = SamplingContext(
            transactionContext = createFakeTransactionContext(name = "different"),
        )
        val differentSampleRate = options.tracesSampler?.invoke(differentSamplingContext)
        assertEquals(0.1, differentSampleRate)
    }

    @Test
    fun `tracerSampler returns correct TransactionContext name`() {
        val options = SentryOptions()
        val expectedName = "test"
        var actualName = ""
        options.tracesSampler = {
            actualName = it.transactionContext.name
            null
        }

        val samplingContext = SamplingContext(
            transactionContext = mockTransactionContext,
        )
        options.tracesSampler?.invoke(samplingContext)
        assertEquals(expectedName, actualName)
    }

    @Test
    fun `tracerSampler returns correct TransactionContext spanId`() {
        val options = SentryOptions()
        val expectedSpanId = mockTransactionContext.spanId
        var actualSpanId: SpanId? = null
        options.tracesSampler = {
            actualSpanId = it.transactionContext.spanId
            null
        }

        val samplingContext = SamplingContext(
            transactionContext = mockTransactionContext,
        )
        options.tracesSampler?.invoke(samplingContext)
        assertEquals(expectedSpanId, actualSpanId)
    }

    @Test
    fun `tracerSampler returns correct TransactionContext parentSpanId`() {
        val options = SentryOptions()
        val expectedParentSpanId = mockTransactionContext.parentSpanId
        var actualParentSpanId: SpanId? = null
        options.tracesSampler = {
            actualParentSpanId = it.transactionContext.parentSpanId
            null
        }

        val samplingContext = SamplingContext(
            transactionContext = mockTransactionContext,
        )
        options.tracesSampler?.invoke(samplingContext)
        assertEquals(expectedParentSpanId, actualParentSpanId)
    }

    @Test
    fun `tracerSampler returns correct TransactionContext description`() {
        val options = SentryOptions()
        val expectedDescription = mockTransactionContext.description
        var actualDescription: String? = null
        options.tracesSampler = {
            actualDescription = it.transactionContext.description
            null
        }

        val samplingContext = SamplingContext(
            transactionContext = mockTransactionContext,
        )
        options.tracesSampler?.invoke(samplingContext)
        assertEquals(expectedDescription, actualDescription)
    }

    @Test
    fun `tracerSampler returns correct TransactionContext sampled`() {
        val options = SentryOptions()
        val expectedSampled = mockTransactionContext.sampled
        var actualSampled: Boolean? = null
        options.tracesSampler = {
            actualSampled = it.transactionContext.sampled
            null
        }

        val samplingContext = SamplingContext(
            transactionContext = mockTransactionContext,
        )
        options.tracesSampler?.invoke(samplingContext)
        assertEquals(expectedSampled, actualSampled)
    }

    @Test
    fun `tracerSampler returns correct TransactionContext parentSampled`() {
        val options = SentryOptions()
        val expectedParentSampled = mockTransactionContext.parentSampled
        var actualParentSampled: Boolean? = null
        options.tracesSampler = {
            actualParentSampled = it.transactionContext.parentSampled
            null
        }

        val samplingContext = SamplingContext(
            transactionContext = mockTransactionContext,
        )
        options.tracesSampler?.invoke(samplingContext)
        assertEquals(expectedParentSampled, actualParentSampled)
    }

    @Test
    fun `tracerSampler returns correct TransactionContext operation`() {
        val options = SentryOptions()
        val expectedOperation = mockTransactionContext.operation
        var actualOperation: String? = null
        options.tracesSampler = {
            actualOperation = it.transactionContext.operation
            null
        }

        val samplingContext = SamplingContext(
            transactionContext = mockTransactionContext,
        )
        options.tracesSampler?.invoke(samplingContext)
        assertEquals(expectedOperation, actualOperation)
    }

    @Test
    fun `tracerSampler returns correct TransactionContext transactionNameSource`() {
        val options = SentryOptions()
        val expectedTransactionNameSource = mockTransactionContext.transactionNameSource
        var actualTransactionNameSource: TransactionNameSource? = null
        options.tracesSampler = {
            actualTransactionNameSource = it.transactionContext.transactionNameSource
            null
        }

        val samplingContext = SamplingContext(
            transactionContext = mockTransactionContext,
        )
        options.tracesSampler?.invoke(samplingContext)
        assertEquals(expectedTransactionNameSource, actualTransactionNameSource)
    }
}
