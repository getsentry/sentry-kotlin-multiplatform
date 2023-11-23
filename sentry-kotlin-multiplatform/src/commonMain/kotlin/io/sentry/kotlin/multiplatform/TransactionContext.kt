package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource

/** An interface representing the context of a transaction. */
public interface TransactionContext : SpanContext {
    public val name: String
    public val transactionNameSource: TransactionNameSource
    public val parentSampled: Boolean
}

internal class TransactionContextAdapter constructor(private val transactionContext: TransactionContext) :
    TransactionContext by transactionContext