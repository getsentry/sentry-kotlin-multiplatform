package io.sentry.kotlin.multiplatform

/**
 * Context used by [io.sentry.kotlin.multiplatform.SentryOptions.tracesSampler] to determine if transaction
 * is going to be sampled.
 */
public data class SamplingContext(
    /** The transaction context. */
    public val transactionContext: TransactionContext
)
