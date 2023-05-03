package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaSampleDecision
import io.sentry.kotlin.multiplatform.CocoaSentryId
import io.sentry.kotlin.multiplatform.CocoaSpanId
import io.sentry.kotlin.multiplatform.CocoaTransactionContext
import io.sentry.kotlin.multiplatform.TransactionContext
import kotlinx.cinterop.UnsafeNumber

@OptIn(UnsafeNumber::class)
internal fun TransactionContext.toCocoa() = CocoaTransactionContext(
    name = name,
    operation = operation,
    traceId = CocoaSentryId(traceId.toString()),
    spanId = CocoaSpanId(spanId.toString()),
    parentSpanId = parentSpanId?.let { CocoaSpanId(it.toString()) },
    parentSampled = parentSampled?.toSampleDecision()
        ?: CocoaSampleDecision.kSentrySampleDecisionNo,
)
