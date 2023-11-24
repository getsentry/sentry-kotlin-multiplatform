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
    public val parentSampled: Boolean
}

/** The default Transaction Context implementation. */
public class TransactionContextImpl(override val operation: String) : TransactionContext {
    override var name: String = "<unlabeled transaction>"
    override var sampled: Boolean? = null
    override var parentSampled: Boolean = false
    override var traceId: SentryId = SentryId.EMPTY_ID
    override var spanId: SpanId = SpanId.EMPTY_ID
    override var parentSpanId: SpanId? = null
    override var description: String? = null
    override var transactionNameSource: TransactionNameSource = TransactionNameSource.CUSTOM

    public constructor(operation: String, name: String) : this(operation) {
        this.name = name
    }

    public constructor(operation: String, name: String, sampled: Boolean?) : this(operation, name) {
        this.name = name
        this.sampled = sampled
    }
}

public fun TransactionContext(operation: String): TransactionContext = TransactionContextImpl(operation)
public fun TransactionContext(operation: String, name: String): TransactionContext =
    TransactionContextImpl(operation, name)
public fun TransactionContext(operation: String, name: String, sampled: Boolean): TransactionContext =
    TransactionContextImpl(operation, name, sampled)
