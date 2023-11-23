package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaSpanStatus
import io.sentry.kotlin.multiplatform.SpanStatus

internal fun SpanStatus.toCocoa(): CocoaSpanStatus {
    return when (this) {
        SpanStatus.ABORTED -> CocoaSpanStatus.kSentrySpanStatusAborted
        SpanStatus.ALREADY_EXISTS -> CocoaSpanStatus.kSentrySpanStatusAlreadyExists
        SpanStatus.CANCELLED -> CocoaSpanStatus.kSentrySpanStatusCancelled
        SpanStatus.DATA_LOSS -> CocoaSpanStatus.kSentrySpanStatusDataLoss
        SpanStatus.DEADLINE_EXCEEDED -> CocoaSpanStatus.kSentrySpanStatusDeadlineExceeded
        SpanStatus.FAILED_PRECONDITION -> CocoaSpanStatus.kSentrySpanStatusFailedPrecondition
        SpanStatus.INVALID_ARGUMENT -> CocoaSpanStatus.kSentrySpanStatusInvalidArgument
        SpanStatus.NOT_FOUND -> CocoaSpanStatus.kSentrySpanStatusNotFound
        SpanStatus.OK -> CocoaSpanStatus.kSentrySpanStatusOk
        SpanStatus.OUT_OF_RANGE -> CocoaSpanStatus.kSentrySpanStatusOutOfRange
        SpanStatus.PERMISSION_DENIED -> CocoaSpanStatus.kSentrySpanStatusPermissionDenied
        SpanStatus.RESOURCE_EXHAUSTED -> CocoaSpanStatus.kSentrySpanStatusResourceExhausted
        SpanStatus.UNAUTHENTICATED -> CocoaSpanStatus.kSentrySpanStatusUnauthenticated
        SpanStatus.UNAVAILABLE -> CocoaSpanStatus.kSentrySpanStatusUnavailable
        SpanStatus.UNIMPLEMENTED -> CocoaSpanStatus.kSentrySpanStatusUnimplemented
        else -> {
            CocoaSpanStatus.kSentrySpanStatusUnknownError
        }
    }
}

internal fun CocoaSpanStatus.toKmp(): SpanStatus {
    return when (this) {
        CocoaSpanStatus.kSentrySpanStatusAborted -> SpanStatus.ABORTED
        CocoaSpanStatus.kSentrySpanStatusAlreadyExists -> SpanStatus.ALREADY_EXISTS
        CocoaSpanStatus.kSentrySpanStatusCancelled -> SpanStatus.CANCELLED
        CocoaSpanStatus.kSentrySpanStatusDataLoss -> SpanStatus.DATA_LOSS
        CocoaSpanStatus.kSentrySpanStatusDeadlineExceeded -> SpanStatus.DEADLINE_EXCEEDED
        CocoaSpanStatus.kSentrySpanStatusFailedPrecondition -> SpanStatus.FAILED_PRECONDITION
        CocoaSpanStatus.kSentrySpanStatusInvalidArgument -> SpanStatus.INVALID_ARGUMENT
        CocoaSpanStatus.kSentrySpanStatusNotFound -> SpanStatus.NOT_FOUND
        CocoaSpanStatus.kSentrySpanStatusOk -> SpanStatus.OK
        CocoaSpanStatus.kSentrySpanStatusOutOfRange -> SpanStatus.OUT_OF_RANGE
        CocoaSpanStatus.kSentrySpanStatusPermissionDenied -> SpanStatus.PERMISSION_DENIED
        CocoaSpanStatus.kSentrySpanStatusResourceExhausted -> SpanStatus.RESOURCE_EXHAUSTED
        CocoaSpanStatus.kSentrySpanStatusUnauthenticated -> SpanStatus.UNAUTHENTICATED
        CocoaSpanStatus.kSentrySpanStatusUnavailable -> SpanStatus.UNAVAILABLE
        CocoaSpanStatus.kSentrySpanStatusUnimplemented -> SpanStatus.UNIMPLEMENTED
        else -> SpanStatus.UNKNOWN
    }
}
