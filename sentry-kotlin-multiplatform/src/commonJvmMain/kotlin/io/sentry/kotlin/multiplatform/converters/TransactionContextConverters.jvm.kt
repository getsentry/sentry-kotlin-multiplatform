package io.sentry.kotlin.multiplatform.converters

import io.sentry.TracesSamplingDecision
import io.sentry.kotlin.multiplatform.JvmTransactionContext
import io.sentry.kotlin.multiplatform.TransactionContext

/**
 * Converts a [JvmTransactionContext] to a [TransactionContext].
 *
 * Mainly used by the SDK for starting transactions with a [TransactionContext].
 */
internal fun TransactionContext.toJvm(): JvmTransactionContext {
    val sampleDecision = sampled?.let { TracesSamplingDecision(it) }
    val kmpScope = this@toJvm
    return JvmTransactionContext(name, operation, sampleDecision).apply {
        parentSampled = kmpScope.parentSampled
        description = kmpScope.description
        name = kmpScope.name
    }
}
