package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.CocoaSentryId
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
public actual data class SentryId actual constructor(val sentryIdString: String) {

    public actual companion object {
        public actual val EMPTY_ID: SentryId = SentryId("")
    }

    private var cocoaSentryId: CocoaSentryId? = null

    init {
        cocoaSentryId = if (sentryIdString.isEmpty()) {
            CocoaSentryId.empty()
        } else {
            CocoaSentryId(sentryIdString)
        }
    }

    actual override fun toString(): String {
        return cocoaSentryId.toString()
    }
}
