@file:OptIn(ExperimentalForeignApi::class)

package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.CocoaSpanId

public actual data class SpanId actual constructor(val spanIdString: String) {
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

    public actual companion object {
        public actual val EMPTY_ID: SpanId = SpanId("")
    }
}
