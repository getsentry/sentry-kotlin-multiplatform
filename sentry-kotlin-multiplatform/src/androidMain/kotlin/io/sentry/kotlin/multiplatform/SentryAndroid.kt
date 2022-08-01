package io.sentry.kotlin.multiplatform

import io.sentry.Sentry

private val globalScope = SentryScope()

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {
        val androidSentryId = Sentry.captureMessage(message)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureMessage(message: String, scopeCallback: SentryScopeCallback): SentryId {
        val localScope = SentryScope()
        val scopeConfiguration = localScope.scopeConfiguration(scopeCallback)
        val androidSentryId = Sentry.captureMessage(message, scopeConfiguration)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val androidSentryId = Sentry.captureException(throwable)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureException(throwable: Throwable, scopeCallback: SentryScopeCallback): SentryId {
        val localScope = SentryScope()
        val scopeConfiguration = localScope.scopeConfiguration(scopeCallback)
        val androidSentryId = Sentry.captureException(throwable, scopeConfiguration)
        return SentryId(androidSentryId.toString())
    }

    actual fun configureScope(scopeCallback: SentryScopeCallback) {
        val scopeConfiguration = globalScope.scopeConfiguration(scopeCallback)
        Sentry.configureScope(scopeConfiguration)
    }

    actual fun close() {
        Sentry.close()
    }
}
