package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaSentryId
import io.sentry.kotlin.multiplatform.CocoaUserFeedback
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

fun UserFeedback.toCocoaUserFeedback() = CocoaUserFeedback(CocoaSentryId(sentryId.toString())).apply {
    comments = this@toCocoaUserFeedback.comments.toString()
    email = this@toCocoaUserFeedback.email.toString()
    name = this@toCocoaUserFeedback.name.toString()
}

