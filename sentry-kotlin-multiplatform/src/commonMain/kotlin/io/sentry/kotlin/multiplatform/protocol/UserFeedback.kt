package io.sentry.kotlin.multiplatform.protocol

data class UserFeedback(internal val sentryId: SentryId) {

    /** The user's name */
    var name: String? = null

    /** The user's email */
    var email: String? = null

    /** The user's comment */
    var comments: String? = null
}
