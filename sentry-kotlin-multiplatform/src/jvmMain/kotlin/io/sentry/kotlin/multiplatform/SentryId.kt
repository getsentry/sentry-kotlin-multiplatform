package io.sentry.kotlin.multiplatform

import io.sentry.protocol.SentryId as JvmSentryId

actual class SentryId actual constructor(sentryIdString: String): ISentryId {

    actual companion object {
        actual val EMPTY_ID: SentryId = SentryId("")
    }

    private var jvmSentryId: JvmSentryId? = null

    init {
        jvmSentryId = if (sentryIdString.isEmpty()) JvmSentryId.EMPTY_ID
        else JvmSentryId(sentryIdString)
    }

    actual override fun toString(): String {
        return jvmSentryId.toString()
    }
}
