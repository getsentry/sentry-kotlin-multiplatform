package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryFeedbackSourceCustom
import io.sentry.kotlin.multiplatform.CocoaSentryId
import io.sentry.kotlin.multiplatform.CocoaUserFeedback
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

internal fun UserFeedback.toCocoaUserFeedback(): CocoaUserFeedback =
    CocoaUserFeedback(
        message = comments.orEmpty(),
        name = name,
        email = email,
        source = SentryFeedbackSourceCustom,
        associatedEventId = CocoaSentryId(sentryId.toString()),
        attachments = null,
    )
