package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

internal expect class SentryBridge(sentryInstance: SentryInstance = SentryPlatformInstance()) {
    fun init(context: Context, configuration: OptionsConfiguration)

    fun init(configuration: OptionsConfiguration)

    fun initWithPlatformOptions(configuration: PlatformOptionsConfiguration)

    fun captureMessage(message: String): SentryId

    fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId

    fun captureException(throwable: Throwable): SentryId

    fun captureException(throwable: Throwable, scopeCallback: ScopeCallback): SentryId

    fun configureScope(scopeCallback: ScopeCallback)

    fun captureUserFeedback(userFeedback: UserFeedback)

    fun addBreadcrumb(breadcrumb: Breadcrumb)

    fun setUser(user: User?)

    fun isCrashedLastRun(): Boolean

    fun close()
}
