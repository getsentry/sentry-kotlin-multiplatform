package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId
import io.sentry.kotlin.multiplatform.protocol.TransactionNameSource

/** The Transaction Context defines the metadata for a Performance Monitoring Transaction. */
public interface TransactionContext : SpanContext {
    /** The name of the transaction. */
    public val name: String

    /** The source of the transaction name. */
    public val transactionNameSource: TransactionNameSource

    /** Indicates if the parent transaction is sampled. */
    public val parentSampled: Boolean?
}

/** The default Transaction Context implementation. */
internal class TransactionContextImpl(override val operation: String) : TransactionContext {
    override var name: String = "<unlabeled transaction>"
    override var sampled: Boolean? = null
    override var parentSampled: Boolean? = null
    override var traceId: SentryId = SentryId.EMPTY_ID
    override var spanId: SpanId = SpanId.EMPTY_ID
    override var parentSpanId: SpanId? = null
    override var description: String? = null
    override var transactionNameSource: TransactionNameSource = TransactionNameSource.CUSTOM

    constructor(operation: String, name: String) : this(operation) {
        this.name = name
    }

    constructor(operation: String, name: String, sampled: Boolean?) : this(operation, name) {
        this.sampled = sampled
    }
}

public fun TransactionContext(operation: String): TransactionContext = TransactionContextImpl(operation)

public fun TransactionContext(name: String, operation: String): TransactionContext =
    TransactionContextImpl(name = name, operation = operation)

public fun TransactionContext(name: String, operation: String, sampled: Boolean): TransactionContext =
    TransactionContextImpl(name = name, operation = operation, sampled = sampled)
