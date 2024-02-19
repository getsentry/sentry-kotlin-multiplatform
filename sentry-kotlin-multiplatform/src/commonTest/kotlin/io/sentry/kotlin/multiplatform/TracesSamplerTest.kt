package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.fakes.createFakeCustomSamplingContext
import io.sentry.kotlin.multiplatform.fakes.createFakeTransactionContext
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId
import io.sentry.kotlin.multiplatform.utils.fakeSentryIdString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TracesSamplerTest {
    class Fixture {
        val options = SentryOptions()

        private fun getSamplingContext(
            transactionContext: TransactionContext,
            customSamplingContext: CustomSamplingContext
        ): SamplingContext {
            return SamplingContext(transactionContext, customSamplingContext)
        }

        internal fun getSut(
            transactionContext: TransactionContext = createFakeTransactionContext(),
            customSamplingContext: CustomSamplingContext = createFakeCustomSamplingContext(),
            sampler: (SamplingContext) -> Double?
        ): SamplingContext {
            this.options.tracesSampler = sampler
            return getSamplingContext(transactionContext, customSamplingContext)
        }
    }

    /** Returns the sample rate or null if not sampled. */
    private fun SamplingContext.getSampleRate(): Double? {
        return fixture.options.tracesSampler?.invoke(this)
    }

    private lateinit var fixture: Fixture

    @BeforeTest
    fun setup() {
        fixture = Fixture()
    }

    @Test
    fun `GIVEN null sampleRate WHEN tracesSampler callback set to sampleRate THEN return null sample rate`() {
        // GIVEN
        val sampleRate = null

        // WHEN
        val sut = fixture.getSut { sampleRate }

        // THEM
        assertEquals(sampleRate, sut.getSampleRate())
    }

    @Test
    fun `GIVEN sampleRate WHEN traceSampler callback set to sampleRate THEN return correct sample rate`() {
        // GIVEN
        val sampleRate = 0.5

        // WHEN
        val sut = fixture.getSut { sampleRate }

        // THEN
        assertEquals(sampleRate, sut.getSampleRate())
    }

    @Test
    fun `GIVEN different sampleRates WHEN traceSampler callback set to sampleRates THEN can return different sample rates`() {
        // GIVEN
        val sampleRate1 = 0.5
        val sampleRate2 = 0.1

        // WHEN
        val sut = fixture.getSut {
            if (it.transactionContext.name == "random") {
                sampleRate1
            } else {
                sampleRate2
            }
        }

        // THEN
        assertEquals(sampleRate2, sut.getSampleRate())
    }

    @Test
    fun `GIVEN null sampleRate WHEN traceSampler callback set to sampleRate THEN return null`() {
        // GIVEN
        val sampleRate = null

        // WHEN
        val sut = fixture.getSut { sampleRate }

        // THEN
        assertNull(sut.getSampleRate())
    }

    @Test
    fun `GIVEN transactionContext name WHEN tracerSampler set AND sampled THEN returns correct TransactionContext name`() {
        // GIVEN
        val expectedName = "testName"
        val transactionContext = createFakeTransactionContext(name = expectedName)

        // WHEN
        var actualName = ""
        val sut = fixture.getSut(transactionContext = transactionContext) {
            actualName = it.transactionContext.name
            null
        }
        sut.getSampleRate()

        // THEN
        assertEquals(expectedName, actualName)
    }

    @Test
    fun `GIVEN transactionContext spanId WHEN tracerSampler set AND sampled THEN returns correct TransactionContext spanId`() {
        // GIVEN
        val expectedSpanId = SpanId(fakeSentryIdString)
        val transactionContext = createFakeTransactionContext(spanId = expectedSpanId)

        // WHEN
        var actualSpanId: SpanId? = null
        val sut = fixture.getSut(transactionContext = transactionContext) {
            actualSpanId = it.transactionContext.spanId
            null
        }
        sut.getSampleRate()

        // THEN
        assertEquals(expectedSpanId, actualSpanId)
    }

    @Test
    fun `GIVEN transactionContext parentSpanId WHEN tracerSampler set AND sampled THEN returns correct TransactionContext parentSpanId`() {
        // GIVEN
        val expectedParentSpanId = SpanId("123")
        val transactionContext = createFakeTransactionContext(parentSpanId = expectedParentSpanId)

        // WHEN
        var actualParentSpanId: SpanId? = null
        val sut = fixture.getSut(transactionContext = transactionContext) {
            actualParentSpanId = it.transactionContext.parentSpanId
            null
        }
        sut.getSampleRate()

        // THEN
        assertEquals(expectedParentSpanId, actualParentSpanId)
    }

    @Test
    fun `GIVEN transactionContext description WHEN tracerSampler set AND sampled THEN returns correct TransactionContext description`() {
        // GIVEN
        val expectedDescription = "testDescription"
        val transactionContext = createFakeTransactionContext(description = expectedDescription)

        // WHEN
        var actualDescription: String? = null
        val sut = fixture.getSut(transactionContext = transactionContext) {
            actualDescription = it.transactionContext.description
            null
        }
        sut.getSampleRate()

        // THEN
        assertEquals(expectedDescription, actualDescription)
    }

    @Test
    fun `GIVEN transactionContext sampled WHEN tracerSampler set AND sampled THEN returns correct TransactionContext sampled`() {
        // GIVEN
        val expectedSampled = false
        val transactionContext = createFakeTransactionContext(sampled = expectedSampled)

        // WHEN
        var actualSampled: Boolean? = null
        val sut = fixture.getSut(transactionContext = transactionContext) {
            actualSampled = it.transactionContext.sampled
            null
        }
        sut.getSampleRate()

        // THEN
        assertEquals(expectedSampled, actualSampled)
    }

    @Test
    fun `GIVEN transactionContext parentSampled WHEN tracerSampler set AND sampled THEN returns correct TransactionContext parentSampled`() {
        // GIVEN
        val expectedParentSampled = false
        val transactionContext = createFakeTransactionContext(parentSampled = expectedParentSampled)

        // WHEN
        var actualParentSampled: Boolean? = null
        val sut = fixture.getSut(transactionContext = transactionContext) {
            actualParentSampled = it.transactionContext.parentSampled
            null
        }
        sut.getSampleRate()

        // THEN
        assertEquals(expectedParentSampled, actualParentSampled)
    }

    @Test
    fun `GIVEN transactionContext operation WHEN tracerSampler set AND sampled THEN returns correct TransactionContext operation`() {
        // GIVEN
        val expectedOperation = "testOperation"
        val transactionContext = createFakeTransactionContext(operation = expectedOperation)

        // WHEN
        var actualOperation: String? = null
        val sut = fixture.getSut(transactionContext = transactionContext) {
            actualOperation = it.transactionContext.operation
            null
        }
        sut.getSampleRate()

        // THEN
        assertEquals(expectedOperation, actualOperation)
    }

    @Test
    fun `GIVEN transactionContext traceId WHEN tracerSampler set AND sampled THEN returns correct TransactionContext traceId`() {
        // GIVEN
        val expectedTraceId = SentryId(fakeSentryIdString)
        val transactionContext = createFakeTransactionContext(traceId = expectedTraceId)

        // WHEN
        var actualTraceId: SentryId? = null
        val sut = fixture.getSut(transactionContext = transactionContext) {
            actualTraceId = it.transactionContext.traceId
            null
        }
        sut.getSampleRate()

        // THEN
        assertEquals(expectedTraceId, actualTraceId)
    }

    @Test
    fun `GIVEN customSamplingContext WHEN tracerSampler set AND sampled THEN returns correct customSamplingContext`() {
        // GIVEN
        val expectedCustomSamplingContext = mapOf("user_id" to 12345)

        // WHEN
        var actualCustomSamplingContext: CustomSamplingContext = null
        val sut = fixture.getSut(customSamplingContext = expectedCustomSamplingContext) {
            actualCustomSamplingContext = it.customSamplingContext
            null
        }
        sut.getSampleRate()

        // THEN
        assertEquals(expectedCustomSamplingContext, actualCustomSamplingContext)
    }
}
