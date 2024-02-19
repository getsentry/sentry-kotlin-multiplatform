package io.sentry.kotlin.multiplatform.fakes

import io.sentry.kotlin.multiplatform.CustomSamplingContext
import io.sentry.kotlin.multiplatform.TransactionContext
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId
import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource

class FakeTransactionContext(
    override val operation: String,
    override val traceId: SentryId,
    override val spanId: SpanId,
    override val parentSpanId: SpanId?,
    override val description: String?,
    override val sampled: Boolean?,
    override val name: String,
    override val transactionNameSource: TransactionNameSource,
    override val parentSampled: Boolean?
) : TransactionContext

fun createFakeTransactionContext(
    operation: String = "test",
    traceId: SentryId = SentryId.EMPTY_ID,
    spanId: SpanId = SpanId.EMPTY_ID,
    parentSpanId: SpanId? = SpanId("123"),
    description: String? = "test description",
    sampled: Boolean? = null,
    name: String = "test",
    transactionNameSource: TransactionNameSource = TransactionNameSource.TASK,
    parentSampled: Boolean? = null
): TransactionContext {
    return FakeTransactionContext(
        operation = operation,
        traceId = traceId,
        spanId = spanId,
        parentSpanId = parentSpanId,
        description = description,
        sampled = sampled,
        name = name,
        transactionNameSource = transactionNameSource,
        parentSampled = parentSampled
    )
}

fun createFakeCustomSamplingContext(): CustomSamplingContext {
    return mapOf("user_id" to 12345, "search_results" to "search results")
}
