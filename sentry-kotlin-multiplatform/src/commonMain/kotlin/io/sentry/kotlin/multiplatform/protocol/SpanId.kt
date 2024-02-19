package io.sentry.kotlin.multiplatform.protocol

/** The unique ID of a [io.sentry.kotlin.multiplatform.Span]. */
public expect class SpanId(spanIdString: String) {
    public companion object {
        /** Returns a new empty SpanId */
        public val EMPTY_ID: SpanId
    }
    override fun toString(): String
}
