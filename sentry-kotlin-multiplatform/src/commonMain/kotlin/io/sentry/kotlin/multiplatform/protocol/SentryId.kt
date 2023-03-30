package io.sentry.kotlin.multiplatform.protocol

public expect class SentryId(sentryIdString: String) {
    public companion object {
        public val EMPTY_ID: SentryId
    }
    override fun toString(): String
}
