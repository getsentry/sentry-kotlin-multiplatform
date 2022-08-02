package io.sentry.kotlin.multiplatform

internal expect object SentryBridge {

    fun captureMessage(message: String): SentryId

    fun captureMessage(message: String, scopeCallback: (SentryScope) -> Unit): SentryId

    fun captureException(throwable: Throwable): SentryId

    fun captureException(throwable: Throwable, scopeCallback: (SentryScope) -> Unit): SentryId

    fun configureScope(scopeCallback: (SentryScope) -> Unit)

    fun close()
}
