package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryBreadcrumb
import cocoapods.Sentry.SentryId
import cocoapods.Sentry.SentryLevel
import cocoapods.Sentry.SentryOptions
import cocoapods.Sentry.SentryUser
import cocoapods.Sentry.SentryScope
import cocoapods.Sentry.SentryUserFeedback

internal typealias CocoaUser = SentryUser
internal typealias CocoaBreadcrumb = SentryBreadcrumb
internal typealias CocoaSentryOptions = SentryOptions
internal typealias CocoaScope = SentryScope
internal typealias CocoaSentryId = SentryId
internal typealias CocoaSentryLevel = SentryLevel
internal typealias CocoaUserFeedback = SentryUserFeedback
