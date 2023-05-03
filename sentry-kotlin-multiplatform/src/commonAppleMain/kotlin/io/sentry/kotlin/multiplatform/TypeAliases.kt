package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryAttachment
import cocoapods.Sentry.SentryBreadcrumb
import cocoapods.Sentry.SentryEvent
import cocoapods.Sentry.SentryException
import cocoapods.Sentry.SentryId
import cocoapods.Sentry.SentryLevel
import cocoapods.Sentry.SentryMessage
import cocoapods.Sentry.SentryOptions
import cocoapods.Sentry.SentrySampleDecision
import cocoapods.Sentry.SentryScope
import cocoapods.Sentry.SentrySpanId
import cocoapods.Sentry.SentrySpanStatus
import cocoapods.Sentry.SentryTransactionContext
import cocoapods.Sentry.SentryUser
import cocoapods.Sentry.SentryUserFeedback

internal typealias CocoaUser = SentryUser
internal typealias CocoaBreadcrumb = SentryBreadcrumb
internal typealias CocoaSentryOptions = SentryOptions
internal typealias CocoaScope = SentryScope
internal typealias CocoaSentryId = SentryId
internal typealias CocoaSentryLevel = SentryLevel
internal typealias CocoaAttachment = SentryAttachment
internal typealias CocoaUserFeedback = SentryUserFeedback
internal typealias CocoaSentryEvent = SentryEvent
internal typealias CocoaMessage = SentryMessage
internal typealias CocoaSentryException = SentryException
internal typealias CocoaSpanStatus = SentrySpanStatus
internal typealias CocoaSpanId = SentrySpanId
internal typealias CocoaTransactionContext = SentryTransactionContext
internal typealias CocoaSampleDecision = SentrySampleDecision
