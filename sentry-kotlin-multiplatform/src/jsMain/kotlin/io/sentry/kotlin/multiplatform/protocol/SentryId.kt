package io.sentry.kotlin.multiplatform.protocol

actual class SentryId actual constructor(private val sentryIdString: String) : ISentryId {

    actual companion object {
        actual val EMPTY_ID: SentryId
            get() = SentryId("00000000000000000000000000000000")
    }

    actual override fun toString(): String {
        return sentryIdString
    }
}
