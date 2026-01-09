package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaSentryId
import io.sentry.kotlin.multiplatform.CocoaUserFeedback
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

internal fun UserFeedback.toCocoaUserFeedback(): CocoaUserFeedback {
    val sentryId = CocoaSentryId(sentryId.toString())
    return CocoaUserFeedback(sentryId).apply {
        this@toCocoaUserFeedback.comments?.let { setComments(it) }
        this@toCocoaUserFeedback.email?.let { setEmail(it) }
        this@toCocoaUserFeedback.name?.let { setName(it) }
    }
}
