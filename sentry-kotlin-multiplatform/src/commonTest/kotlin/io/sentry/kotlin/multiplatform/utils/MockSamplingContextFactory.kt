package io.sentry.kotlin.multiplatform.utils

import io.sentry.kotlin.multiplatform.CustomSamplingContext
import io.sentry.kotlin.multiplatform.MockTransactionContext
import io.sentry.kotlin.multiplatform.TransactionContext
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId
import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource

fun createMockTransactionContext(
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
    return MockTransactionContext(
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

fun createMockCustomSamplingContext(data: Map<String, String> = mapOf("test" to "test")): CustomSamplingContext {
    return CustomSamplingContext(data)
}