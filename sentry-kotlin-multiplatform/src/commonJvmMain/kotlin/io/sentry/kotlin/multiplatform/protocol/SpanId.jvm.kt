package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.JvmSpanId

public actual data class SpanId actual constructor(val spanIdString: String) {

    public actual companion object {
        public actual val EMPTY_ID: SpanId = SpanId("")
    }

    private var jvmSpanId: JvmSpanId? = null

    init {
        jvmSpanId = if (spanIdString.isEmpty()) {
            JvmSpanId.EMPTY_ID
        } else {
            JvmSpanId(spanIdString)
        }
    }

    actual override fun toString(): String {
        return jvmSpanId.toString()
    }
}
