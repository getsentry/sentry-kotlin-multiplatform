package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.AndroidSentryId

actual data class SentryId actual constructor(val sentryIdString: String): ISentryId {

    actual companion object {
        actual val EMPTY_ID: SentryId = SentryId("")
    }

    private var androidSentryId: AndroidSentryId? = null

    init {
        androidSentryId = if (sentryIdString.isEmpty()) io.sentry.protocol.SentryId.EMPTY_ID
        else AndroidSentryId(sentryIdString)
    }

    actual override fun toString(): String {
        return androidSentryId.toString()
    }
}
