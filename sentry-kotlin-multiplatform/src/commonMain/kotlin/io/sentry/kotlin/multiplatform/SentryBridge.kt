package io.sentry.kotlin.multiplatform

internal expect object SentryBridge {

    fun captureMessage(message: String): SentryId

    fun captureMessage(message: String, scopeCallback: SentryScopeCallback): SentryId

    fun captureException(throwable: Throwable): SentryId

    fun captureException(throwable: Throwable, scopeCallback: SentryScopeCallback): SentryId

    fun configureScope(scopeCallback: SentryScopeCallback)

    fun close()
}
