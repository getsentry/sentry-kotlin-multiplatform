package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.log.SentryLogger
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

@Suppress("UnusedPrivateMember")
internal actual class SentryBridge actual constructor(
    private val sentryInstance: SentryInstance
) {
    actual fun init(context: Context, configuration: OptionsConfiguration) {
        // No-op
    }

    actual fun init(configuration: OptionsConfiguration) {
        // No-op
    }

    actual fun initWithPlatformOptions(configuration: PlatformOptionsConfiguration) {
        // No-op
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
        // No-op
    }

    actual fun captureUserFeedback(userFeedback: UserFeedback) {
        // No-op
    }

    actual fun addBreadcrumb(breadcrumb: Breadcrumb) {
        // No-op
    }

    actual fun setUser(user: User?) {
        // No-op
    }

    actual fun logger(): SentryLogger {
        return NoOpSentryLogger()
    }

    actual fun isCrashedLastRun(): Boolean {
        return false
    }

    actual fun isEnabled(): Boolean {
        return false
    }

    actual fun close() {
        // No-op
    }
}
