package io.sentry.kotlin.multiplatform

import io.sentry.protocol.SentryId as AndroidSentryId

actual class SentryId actual constructor(sentryIdString: String): ISentryId {
    actual override val EMPTY_ID = SentryId("")
    private var androidSentryId: AndroidSentryId? = null

    init {
        androidSentryId = if (sentryIdString.isEmpty()) AndroidSentryId.EMPTY_ID
        else AndroidSentryId(sentryIdString)
    }

    actual override fun toString(): String {
        return androidSentryId.toString()
    }
}
