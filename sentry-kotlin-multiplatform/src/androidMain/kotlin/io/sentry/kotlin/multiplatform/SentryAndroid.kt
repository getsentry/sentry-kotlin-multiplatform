package io.sentry.kotlin.multiplatform

import io.sentry.Sentry

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {
        val androidSentryId = Sentry.captureMessage(message)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureMessage(message: String, scopeCallback: (SentryScope) -> Unit): SentryId {
        val scope = SentryScope()
        val scopeConfiguration = scope.scopeConfiguration(scopeCallback)
        val androidSentryId = Sentry.captureMessage(message, scopeConfiguration)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val androidSentryId = Sentry.captureException(throwable)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureException(throwable: Throwable, scopeCallback: (SentryScope) -> Unit): SentryId {
        val scope = SentryScope()
        val scopeConfiguration = scope.scopeConfiguration(scopeCallback)
        val androidSentryId = Sentry.captureException(throwable, scopeConfiguration)
        return SentryId(androidSentryId.toString())
    }

    actual fun configureScope(scopeCallback: (SentryScope) -> Unit) {
        val scope = SentryScope()
        val scopeConfiguration = scope.scopeConfiguration(scopeCallback)
        Sentry.configureScope(scopeConfiguration)
    }

    actual fun close() {
        Sentry.close()
    }
}
