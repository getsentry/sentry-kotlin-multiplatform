package io.sentry.kotlin.multiplatform

public data class SamplingContext(
    public val transactionContext: TransactionContext,
    public val customSamplingContext: CustomSamplingContext?
)
