package io.sentry.kotlin.multiplatform.protocol

actual class SentryId actual constructor(sentryIdString: String) : ISentryId {
    actual companion object {
        actual val EMPTY_ID: SentryId
            get() = SentryId("")
    }

    actual override fun toString(): String {
        return ""
    }
}
