package io.sentry.kotlin.multiplatform.protocol

/** The Sentry event ID */
public expect class SentryId(sentryIdString: String) {
    public companion object {
        /** Returns a new empty SentryId */
        public val EMPTY_ID: SentryId
    }
    override fun toString(): String
}
