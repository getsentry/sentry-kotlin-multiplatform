package io.sentry.kotlin.multiplatform.protocol

/** UserFeedback adds additional information about what happened to an event. */
public data class UserFeedback(
    /** The Sentry event ID */
    val sentryId: SentryId
) {
    /** The user's name */
    var name: String? = null

    /** The user's email */
    var email: String? = null

    /** The user's comment */
    var comments: String? = null
}
