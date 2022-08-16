package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.JvmSentryId

actual data class SentryId actual constructor(val sentryIdString: String) : ISentryId {

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
