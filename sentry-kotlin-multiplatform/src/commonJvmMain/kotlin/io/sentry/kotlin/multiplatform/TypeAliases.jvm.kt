package io.sentry.kotlin.multiplatform

import io.sentry.Attachment
import io.sentry.Breadcrumb
import io.sentry.IScope
import io.sentry.Scope
import io.sentry.SentryAttributeType
import io.sentry.SentryAttributes
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.SentryLogEvent
import io.sentry.SentryLogLevel
import io.sentry.SentryOptions
import io.sentry.SentryReplayOptions
import io.sentry.UserFeedback
import io.sentry.protocol.Contexts
import io.sentry.protocol.Message
import io.sentry.protocol.SentryException
import io.sentry.protocol.SentryId
import io.sentry.protocol.User

internal typealias JvmSentryLevel = SentryLevel
internal typealias JvmUser = User
internal typealias JvmScope = Scope
internal typealias JvmIScope = IScope
internal typealias JvmBreadcrumb = Breadcrumb
internal typealias JvmSentryId = SentryId
internal typealias JvmSentryOptions = SentryOptions
internal typealias JvmAttachment = Attachment
internal typealias JvmUserFeedback = UserFeedback
internal typealias JvmSentryEvent = SentryEvent
internal typealias JvmMessage = Message
internal typealias JvmSentryException = SentryException
internal typealias JvmContexts = Contexts
internal typealias JvmSentryReplayQuality = SentryReplayOptions.SentryReplayQuality
internal typealias JvmSentryAttributeType = SentryAttributeType
internal typealias JvmSentryLog = SentryLogEvent
internal typealias JvmSentryLogLevel = SentryLogLevel
internal typealias JvmSentryAttributes = SentryAttributes
