package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.Sentry

internal actual object SentryBridge {

    actual fun init(context: Context?, configuration: (SentryOptions) -> Unit) {

    }

    actual fun captureMessage(message: String): SentryId {
        val androidSentryId = Sentry.captureMessage(message)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId {
        val androidSentryId = Sentry.captureMessage(message, configureScopeCallback(scopeCallback))
        return SentryId(androidSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val androidSentryId = Sentry.captureException(throwable)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureException(throwable: Throwable, scopeCallback: ScopeCallback): SentryId {
        val androidSentryId = Sentry.captureException(throwable, configureScopeCallback(scopeCallback))
        return SentryId(androidSentryId.toString())
    }

    actual fun configureScope(scopeCallback: ScopeCallback) {
        Sentry.configureScope(configureScopeCallback(scopeCallback))
    }

    actual fun close() {
        Sentry.close()
    }

    private fun configureScopeCallback(scopeCallback: ScopeCallback): (JvmScope) -> Unit {
        return {
            val androidScopeImpl = ScopeJvmImpl(it)
            val scope = Scope(androidScopeImpl)
            scopeCallback.invoke(scope)
        }
    }
}
