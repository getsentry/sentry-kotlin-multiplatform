package io.sentry.kotlin.multiplatform.protocol

public expect class SpanId(spanIdString: String) {
    override fun equals(other: Any?): Boolean

    override fun toString(): String

    override fun hashCode(): Int

    public companion object {
        public val EMPTY_ID: SpanId
    }
}
