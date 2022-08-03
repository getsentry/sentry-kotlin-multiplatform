package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryBreadcrumb
import cocoapods.Sentry.SentryId
import cocoapods.Sentry.SentryOptions
import cocoapods.Sentry.SentryUser
import cocoapods.Sentry.SentryScope

typealias CocoaUser = SentryUser
typealias CocoaBreadcrumb = SentryBreadcrumb
typealias CocoaSentryOptions = SentryOptions
typealias CocoaScope = SentryScope
typealias CocoaSentryId = SentryId
