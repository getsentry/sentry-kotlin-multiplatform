package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId

internal expect object SentryBridge {

    fun captureMessage(message: String): SentryId

    fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId

    fun captureException(throwable: Throwable): SentryId

    fun captureException(throwable: Throwable, scopeCallback: ScopeCallback): SentryId

    fun configureScope(scopeCallback: ScopeCallback)

    fun close()
}
