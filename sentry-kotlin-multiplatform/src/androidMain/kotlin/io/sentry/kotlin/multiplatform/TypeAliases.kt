package io.sentry.kotlin.multiplatform

import io.sentry.Breadcrumb
import io.sentry.Scope
import io.sentry.SentryLevel
import io.sentry.protocol.User

typealias AndroidSentryLevel = SentryLevel
typealias AndroidSentryUser = User
typealias AndroidSentryScope = Scope
typealias AndroidSentryBreadcrumb = Breadcrumb
