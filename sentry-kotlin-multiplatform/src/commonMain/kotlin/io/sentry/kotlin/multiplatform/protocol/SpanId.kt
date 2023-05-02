package io.sentry.kotlin.multiplatform.protocol

public expect class SpanId(spanIdString: String) {
    public companion object {
        public val EMPTY_ID: SpanId
    }
    override fun toString(): String
}