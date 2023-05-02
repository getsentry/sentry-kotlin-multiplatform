package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource

public class TransactionContextProv constructor(private val transactionContext: TransactionContext) :
    TransactionContext by transactionContext

public interface TransactionContext : SpanContext {
    public val name: String
    public val transactionNameSource: TransactionNameSource
    public val parentSampled: Boolean?
}