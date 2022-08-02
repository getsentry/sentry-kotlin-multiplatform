package io.sentry.kotlin.multiplatform.protocol

interface ISentryId {
    companion object {
        val EMPTY_ID: SentryId = SentryId("")
    }
    override fun toString(): String
}

expect class SentryId(sentryIdString: String): ISentryId {
    companion object {
        val EMPTY_ID: SentryId
    }
    override fun toString(): String
}
