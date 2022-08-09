package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryId
import io.sentry.kotlin.multiplatform.JvmUserFeedback
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

fun UserFeedback.toJvmUserFeedback() = JvmUserFeedback(JvmSentryId(sentryId.toString())).apply {
    comments = this@toJvmUserFeedback.comments
    email = this@toJvmUserFeedback.email
    name = this@toJvmUserFeedback.name
}
