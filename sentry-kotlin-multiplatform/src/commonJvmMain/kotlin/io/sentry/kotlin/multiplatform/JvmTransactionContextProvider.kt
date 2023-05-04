package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmp
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId
import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource

internal class JvmTransactionContextProvider(jvmTransactionContext: JvmTransactionContext) :
    TransactionContext {
    override val name: String = jvmTransactionContext.name
    override val transactionNameSource: TransactionNameSource =
        jvmTransactionContext.transactionNameSource.toKmp()
    override val sampled: Boolean = jvmTransactionContext.sampled ?: false
    override val parentSampled: Boolean = jvmTransactionContext.parentSampled ?: false
    override val operation: String = jvmTransactionContext.operation
    override val traceId: SentryId = SentryId(jvmTransactionContext.traceId.toString())
    override val spanId: SpanId = SpanId(jvmTransactionContext.spanId.toString())
    override val parentSpanId: SpanId? =
        jvmTransactionContext.parentSpanId?.let { SpanId(it.toString()) }
    override val description: String? = jvmTransactionContext.description
}