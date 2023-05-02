package io.sentry.kotlin.multiplatform

public data class SamplingContext(
    private val transactionContext: TransactionContext,
    private val customSamplingContext: CustomSamplingContext?
)
