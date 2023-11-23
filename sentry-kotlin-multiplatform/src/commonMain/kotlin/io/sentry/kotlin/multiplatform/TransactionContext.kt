package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource

/** An interface representing the context of a transaction. */
public interface TransactionContext : SpanContext {
    /** The name of the transaction. */
    public val name: String

    /** The source of the transaction name. */
    public val transactionNameSource: TransactionNameSource

    /** Indicates if the parent transaction is sampled. */
    public val parentSampled: Boolean
}

internal class TransactionContextAdapter constructor(private val transactionContext: TransactionContext) :
    TransactionContext by transactionContext