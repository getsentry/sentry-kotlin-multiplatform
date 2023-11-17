package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.CocoaSentryId
import io.sentry.kotlin.multiplatform.CocoaSpanId

public actual data class SpanId actual constructor(val spanIdString: String) {

    public actual companion object {
        public actual val EMPTY_ID: SpanId = SpanId("")
    }

    private var cocoaSpanId: CocoaSpanId? = null

    init {
        cocoaSpanId = if (spanIdString.isEmpty()) {
            CocoaSpanId.empty
        } else {
            CocoaSpanId(spanIdString)
        }
    }

    actual override fun toString(): String {
        return cocoaSpanId.toString()
    }
}
