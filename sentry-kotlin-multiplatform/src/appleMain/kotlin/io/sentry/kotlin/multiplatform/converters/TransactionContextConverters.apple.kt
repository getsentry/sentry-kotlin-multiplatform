package io.sentry.kotlin.multiplatform.converters

import io.sentry.kotlin.multiplatform.CocoaSampleDecision
import io.sentry.kotlin.multiplatform.CocoaTransactionContext
import io.sentry.kotlin.multiplatform.TransactionContext

/**
 * Converts a [CocoaTransactionContext] to a [TransactionContext].
 *
 * Mainly used by the SDK for starting transactions with a [TransactionContext].
 */
internal fun TransactionContext.toCocoa(): CocoaTransactionContext {
    val sampleDecision = sampled?.toSampleDecision() ?: CocoaSampleDecision.kSentrySampleDecisionUndecided
    val kmpScope = this@toCocoa

    return CocoaTransactionContext(name, operation, sampleDecision).apply {
        parentSampled = kmpScope.parentSampled.toSampleDecision()
    }
}
