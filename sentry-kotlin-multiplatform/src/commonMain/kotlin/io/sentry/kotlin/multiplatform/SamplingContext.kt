package io.sentry.kotlin.multiplatform

public typealias CustomSamplingContext = Map<String, Any?>?

/**
 * Context used by [io.sentry.kotlin.multiplatform.SentryOptions.tracesSampler] to determine if transaction
 * is going to be sampled.
 */
public data class SamplingContext(
    /** The transaction context. */
    public val transactionContext: TransactionContext,

    /** Arbitrary data used to determine if transaction is going to be sampled. */
    public val customSamplingContext: CustomSamplingContext
)
