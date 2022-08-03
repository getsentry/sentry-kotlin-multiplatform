package io.sentry.kotlin.multiplatform

import io.sentry.Breadcrumb
import io.sentry.Scope
import io.sentry.SentryLevel
import io.sentry.protocol.SentryId
import io.sentry.protocol.User

typealias AndroidSentryLevel = SentryLevel
typealias AndroidUser = User
typealias AndroidScope = Scope
typealias AndroidBreadcrumb = Breadcrumb
typealias AndroidSentryId = SentryId
