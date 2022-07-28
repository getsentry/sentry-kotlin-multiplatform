package io.sentry.kotlin.multiplatform

expect class SentryId(sentryIdString: String): ISentryId {
    override val EMPTY_ID: SentryId
    override fun toString(): String
}

interface ISentryId {
    val EMPTY_ID: SentryId
    override fun toString(): String
}
