package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

internal actual class SentryBridge actual constructor(
    private val sentryInstance: SentryInstance
) {
    actual fun init(context: Context, configuration: OptionsConfiguration) {
    }

    actual fun init(configuration: OptionsConfiguration) {
    }

    actual fun initWithPlatformOptions(configuration: PlatformOptionsConfiguration) {
    }

    actual fun captureMessage(message: String): SentryId {
        return SentryId.EMPTY_ID
    }

    actual fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId {
        return SentryId.EMPTY_ID
    }

    actual fun captureException(throwable: Throwable): SentryId {
        return SentryId.EMPTY_ID
    }

    actual fun captureException(throwable: Throwable, scopeCallback: ScopeCallback): SentryId {
        return SentryId.EMPTY_ID
    }

    actual fun configureScope(scopeCallback: ScopeCallback) {
    }

    actual fun captureUserFeedback(userFeedback: UserFeedback) {
    }

    actual fun addBreadcrumb(breadcrumb: Breadcrumb) {
    }

    actual fun setUser(user: User?) {
    }

    actual fun isCrashedLastRun(): Boolean {
        return false
    }

    actual fun isEnabled(): Boolean {
        return false
    }

    actual fun close() {
    }
}