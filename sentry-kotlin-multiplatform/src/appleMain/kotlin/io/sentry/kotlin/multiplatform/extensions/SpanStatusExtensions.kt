@file:OptIn(ExperimentalForeignApi::class)

package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaSpanStatus
import io.sentry.kotlin.multiplatform.SpanStatus2
import kotlinx.cinterop.ExperimentalForeignApi

internal fun SpanStatus2.toCocoa(): CocoaSpanStatus {
    return when (this) {
        SpanStatus2.ABORTED -> CocoaSpanStatus.kSentrySpanStatusAborted
        SpanStatus2.ALREADY_EXISTS -> CocoaSpanStatus.kSentrySpanStatusAlreadyExists
        SpanStatus2.CANCELLED -> CocoaSpanStatus.kSentrySpanStatusCancelled
        SpanStatus2.DATA_LOSS -> CocoaSpanStatus.kSentrySpanStatusDataLoss
        SpanStatus2.DEADLINE_EXCEEDED -> CocoaSpanStatus.kSentrySpanStatusDeadlineExceeded
        SpanStatus2.FAILED_PRECONDITION -> CocoaSpanStatus.kSentrySpanStatusFailedPrecondition
        SpanStatus2.INVALID_ARGUMENT -> CocoaSpanStatus.kSentrySpanStatusInvalidArgument
        SpanStatus2.NOT_FOUND -> CocoaSpanStatus.kSentrySpanStatusNotFound
        SpanStatus2.OK -> CocoaSpanStatus.kSentrySpanStatusOk
        SpanStatus2.OUT_OF_RANGE -> CocoaSpanStatus.kSentrySpanStatusOutOfRange
        SpanStatus2.PERMISSION_DENIED -> CocoaSpanStatus.kSentrySpanStatusPermissionDenied
        SpanStatus2.RESOURCE_EXHAUSTED -> CocoaSpanStatus.kSentrySpanStatusResourceExhausted
        SpanStatus2.UNAUTHENTICATED -> CocoaSpanStatus.kSentrySpanStatusUnauthenticated
        SpanStatus2.UNAVAILABLE -> CocoaSpanStatus.kSentrySpanStatusUnavailable
        SpanStatus2.UNIMPLEMENTED -> CocoaSpanStatus.kSentrySpanStatusUnimplemented
        else -> {
            CocoaSpanStatus.kSentrySpanStatusUnknownError
        }
    }
}

internal fun CocoaSpanStatus.toKmp(): SpanStatus2 {
    return when (this) {
        CocoaSpanStatus.kSentrySpanStatusAborted -> SpanStatus2.ABORTED
        CocoaSpanStatus.kSentrySpanStatusAlreadyExists -> SpanStatus2.ALREADY_EXISTS
        CocoaSpanStatus.kSentrySpanStatusCancelled -> SpanStatus2.CANCELLED
        CocoaSpanStatus.kSentrySpanStatusDataLoss -> SpanStatus2.DATA_LOSS
        CocoaSpanStatus.kSentrySpanStatusDeadlineExceeded -> SpanStatus2.DEADLINE_EXCEEDED
        CocoaSpanStatus.kSentrySpanStatusFailedPrecondition -> SpanStatus2.FAILED_PRECONDITION
        CocoaSpanStatus.kSentrySpanStatusInvalidArgument -> SpanStatus2.INVALID_ARGUMENT
        CocoaSpanStatus.kSentrySpanStatusNotFound -> SpanStatus2.NOT_FOUND
        CocoaSpanStatus.kSentrySpanStatusOk -> SpanStatus2.OK
        CocoaSpanStatus.kSentrySpanStatusOutOfRange -> SpanStatus2.OUT_OF_RANGE
        CocoaSpanStatus.kSentrySpanStatusPermissionDenied -> SpanStatus2.PERMISSION_DENIED
        CocoaSpanStatus.kSentrySpanStatusResourceExhausted -> SpanStatus2.RESOURCE_EXHAUSTED
        CocoaSpanStatus.kSentrySpanStatusUnauthenticated -> SpanStatus2.UNAUTHENTICATED
        CocoaSpanStatus.kSentrySpanStatusUnavailable -> SpanStatus2.UNAVAILABLE
        CocoaSpanStatus.kSentrySpanStatusUnimplemented -> SpanStatus2.UNIMPLEMENTED
        else -> SpanStatus2.UNKNOWN
    }
}
