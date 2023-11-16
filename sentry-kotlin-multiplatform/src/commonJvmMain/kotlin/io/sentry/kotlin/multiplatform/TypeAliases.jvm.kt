package io.sentry.kotlin.multiplatform

import io.sentry.Attachment
import io.sentry.Breadcrumb
import io.sentry.Instrumenter
import io.sentry.Scope
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.SpanId
import io.sentry.UserFeedback
import io.sentry.protocol.Contexts
import io.sentry.protocol.Message
import io.sentry.protocol.SentryException
import io.sentry.protocol.SentryId
import io.sentry.protocol.User
import io.sentry.SpanStatus
import io.sentry.TransactionContext
import io.sentry.protocol.TransactionNameSource

internal typealias JvmSentryLevel = SentryLevel
internal typealias JvmUser = User
internal typealias JvmScope = Scope
internal typealias JvmBreadcrumb = Breadcrumb
internal typealias JvmSentryId = SentryId
internal typealias JvmSentryOptions = SentryOptions
internal typealias JvmAttachment = Attachment
internal typealias JvmUserFeedback = UserFeedback
internal typealias JvmSentryEvent = SentryEvent
internal typealias JvmMessage = Message
internal typealias JvmSentryException = SentryException
internal typealias JvmContexts = Contexts
internal typealias JvmSpanStatus = SpanStatus
internal typealias JvmSpanId = SpanId
internal typealias JvmTransactionContext = TransactionContext
internal typealias JvmTransactionNameSource = TransactionNameSource