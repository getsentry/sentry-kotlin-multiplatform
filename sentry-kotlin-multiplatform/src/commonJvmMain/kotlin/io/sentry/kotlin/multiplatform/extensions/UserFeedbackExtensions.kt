package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryId
import io.sentry.kotlin.multiplatform.JvmUserFeedback
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

internal fun UserFeedback.toJvmUserFeedback(): JvmUserFeedback {
    val sentryId = JvmSentryId(sentryId.toString())
    return JvmUserFeedback(sentryId).apply {
        comments = this@toJvmUserFeedback.comments
        email = this@toJvmUserFeedback.email
        name = this@toJvmUserFeedback.name
    }
}
