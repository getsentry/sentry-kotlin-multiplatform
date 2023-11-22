package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaSentryId
import io.sentry.kotlin.multiplatform.CocoaUserFeedback
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

internal fun UserFeedback.toCocoaUserFeedback(): CocoaUserFeedback {
    val sentryId = CocoaSentryId(sentryId.toString())
    return CocoaUserFeedback(sentryId).apply {
        comments = this@toCocoaUserFeedback.comments.toString()
        email = this@toCocoaUserFeedback.email.toString()
        name = this@toCocoaUserFeedback.name.toString()
    }
}
