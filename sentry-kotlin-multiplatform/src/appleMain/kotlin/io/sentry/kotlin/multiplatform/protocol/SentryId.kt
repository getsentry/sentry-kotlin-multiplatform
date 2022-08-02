package io.sentry.kotlin.multiplatform.protocol

actual class SentryId actual constructor(sentryIdString: String): ISentryId {

    actual companion object {
        actual val EMPTY_ID: SentryId = SentryId("")
    }

    private var cocoaSentryId: CocoaSentryId? = null

    init {
        cocoaSentryId = if (sentryIdString.isEmpty()) CocoaSentryId.empty()
        else CocoaSentryId(sentryIdString)
    }

    actual override fun toString(): String {
        return cocoaSentryId.toString()
    }
}
