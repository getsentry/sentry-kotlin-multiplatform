package io.sentry.kotlin.multiplatform

import io.sentry.Breadcrumb
import io.sentry.Scope
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.protocol.SentryId
import io.sentry.protocol.User

internal typealias JvmSentryLevel = SentryLevel
internal typealias JvmUser = User
internal typealias JvmScope = Scope
internal typealias JvmBreadcrumb = Breadcrumb
internal typealias JvmSentryId = SentryId
internal typealias JvmSentryOptions = SentryOptions
