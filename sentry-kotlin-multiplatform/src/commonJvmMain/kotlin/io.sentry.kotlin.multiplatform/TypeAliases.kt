package io.sentry.kotlin.multiplatform

import io.sentry.Breadcrumb
import io.sentry.Scope
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.protocol.SentryId
import io.sentry.protocol.User

typealias JvmSentryLevel = SentryLevel
typealias JvmUser = User
typealias JvmScope = Scope
typealias JvmBreadcrumb = Breadcrumb
typealias JvmSentryId = SentryId
typealias JvmSentryOptions = SentryOptions
