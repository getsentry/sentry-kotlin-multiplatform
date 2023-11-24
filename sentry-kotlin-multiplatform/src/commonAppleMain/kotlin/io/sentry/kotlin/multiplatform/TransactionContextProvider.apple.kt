package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.converters.toBoolean
import io.sentry.kotlin.multiplatform.converters.toKmpTransactionNameSource
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId
import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource

internal class TransactionContextProvider(cocoaTransactionContext: CocoaTransactionContext) :
    TransactionContext {
    override val name: String = cocoaTransactionContext.name
    override val transactionNameSource: TransactionNameSource =
        cocoaTransactionContext.nameSource.toKmpTransactionNameSource()
    override val sampled: Boolean? = cocoaTransactionContext.sampled().toBoolean()
    override val parentSampled: Boolean? = cocoaTransactionContext.parentSampled().toBoolean()
    override val operation: String = cocoaTransactionContext.operation
    override val traceId: SentryId = SentryId(cocoaTransactionContext.traceId.toString())
    override val spanId: SpanId = SpanId(cocoaTransactionContext.spanId.toString())
    override val parentSpanId: SpanId? =
        cocoaTransactionContext.parentSpanId?.let { SpanId(it.toString()) }
    override val description: String? = cocoaTransactionContext.spanDescription
}
