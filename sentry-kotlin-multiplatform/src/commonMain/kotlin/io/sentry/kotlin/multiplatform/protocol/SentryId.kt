package io.sentry.kotlin.multiplatform.protocol

expect class SentryId(sentryIdString: String): ISentryId {
    companion object {
        val EMPTY_ID: SentryId
    }
    override fun toString(): String
}

interface ISentryId {
    companion object {
        val EMPTY_ID: SentryId = SentryId("")
    }
    override fun toString(): String
}
