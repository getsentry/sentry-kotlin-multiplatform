package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSpanStatus
import io.sentry.kotlin.multiplatform.SpanStatus

internal fun SpanStatus.toJvm(): JvmSpanStatus {
    return when (this) {
        SpanStatus.ABORTED -> JvmSpanStatus.ABORTED
        SpanStatus.ALREADY_EXISTS -> JvmSpanStatus.ALREADY_EXISTS
        SpanStatus.CANCELLED -> JvmSpanStatus.CANCELLED
        SpanStatus.DATA_LOSS -> JvmSpanStatus.DATA_LOSS
        SpanStatus.DEADLINE_EXCEEDED -> JvmSpanStatus.DEADLINE_EXCEEDED
        SpanStatus.FAILED_PRECONDITION -> JvmSpanStatus.FAILED_PRECONDITION
        SpanStatus.INVALID_ARGUMENT -> JvmSpanStatus.INVALID_ARGUMENT
        SpanStatus.NOT_FOUND -> JvmSpanStatus.NOT_FOUND
        SpanStatus.OK -> JvmSpanStatus.OK
        SpanStatus.OUT_OF_RANGE -> JvmSpanStatus.OUT_OF_RANGE
        SpanStatus.PERMISSION_DENIED -> JvmSpanStatus.PERMISSION_DENIED
        SpanStatus.RESOURCE_EXHAUSTED -> JvmSpanStatus.RESOURCE_EXHAUSTED
        SpanStatus.UNAUTHENTICATED -> JvmSpanStatus.UNAUTHENTICATED
        SpanStatus.UNAVAILABLE -> JvmSpanStatus.UNAVAILABLE
        SpanStatus.UNIMPLEMENTED -> JvmSpanStatus.UNIMPLEMENTED
        else -> {
            JvmSpanStatus.UNKNOWN
        }
    }
}

internal fun JvmSpanStatus.toKmp(): SpanStatus {
    return when (this) {
        JvmSpanStatus.ABORTED -> SpanStatus.ABORTED
        JvmSpanStatus.ALREADY_EXISTS -> SpanStatus.ALREADY_EXISTS
        JvmSpanStatus.CANCELLED -> SpanStatus.CANCELLED
        JvmSpanStatus.DATA_LOSS -> SpanStatus.DATA_LOSS
        JvmSpanStatus.DEADLINE_EXCEEDED -> SpanStatus.DEADLINE_EXCEEDED
        JvmSpanStatus.FAILED_PRECONDITION -> SpanStatus.FAILED_PRECONDITION
        JvmSpanStatus.INVALID_ARGUMENT -> SpanStatus.INVALID_ARGUMENT
        JvmSpanStatus.NOT_FOUND -> SpanStatus.NOT_FOUND
        JvmSpanStatus.OK -> SpanStatus.OK
        JvmSpanStatus.OUT_OF_RANGE -> SpanStatus.OUT_OF_RANGE
        JvmSpanStatus.PERMISSION_DENIED -> SpanStatus.PERMISSION_DENIED
        JvmSpanStatus.RESOURCE_EXHAUSTED -> SpanStatus.RESOURCE_EXHAUSTED
        JvmSpanStatus.UNAUTHENTICATED -> SpanStatus.UNAUTHENTICATED
        JvmSpanStatus.UNAVAILABLE -> SpanStatus.UNAVAILABLE
        JvmSpanStatus.UNIMPLEMENTED -> SpanStatus.UNIMPLEMENTED
        else -> {
            SpanStatus.UNKNOWN
        }
    }
}
