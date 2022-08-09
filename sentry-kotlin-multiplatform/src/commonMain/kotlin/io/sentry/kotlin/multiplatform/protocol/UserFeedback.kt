package io.sentry.kotlin.multiplatform.protocol

data class UserFeedback(internal val sentryId: SentryId) : IUserFeedback {
    override var name: String? = null
    override var email: String? = null
    override var comments: String? = null
}

interface IUserFeedback {

    /** The user's name */
    var name: String?

    /** The user's email */
    var email: String?

    /** The user's comment */
    var comments: String?
}
