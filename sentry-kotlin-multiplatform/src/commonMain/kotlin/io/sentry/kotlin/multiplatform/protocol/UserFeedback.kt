package io.sentry.kotlin.multiplatform.protocol

data class UserFeedback(internal val sentryId: SentryId) : IUserFeedback {
    override var name: String? = null
    override var email: String? = null
    override var comments: String? = null
}

interface IUserFeedback {

    var name: String?

    var email: String?

    var comments: String?
}
