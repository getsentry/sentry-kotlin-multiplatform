package io.sentry.kotlin.multiplatform.protocol

actual class SentryId actual constructor(sentryIdString: String): ISentryId {

    actual companion object {
        actual val EMPTY_ID: SentryId = SentryId("")
    }

    private var androidSentryId: io.sentry.protocol.SentryId? = null

    init {
        androidSentryId = if (sentryIdString.isEmpty()) io.sentry.protocol.SentryId.EMPTY_ID
        else io.sentry.protocol.SentryId(sentryIdString)
    }

    actual override fun toString(): String {
        return androidSentryId.toString()
    }
}
