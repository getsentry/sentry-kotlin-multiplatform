package io.sentry.kotlin.multiplatform

import io.sentry.Sentry
import io.sentry.kotlin.multiplatform.protocol.SentryId

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {
        val androidSentryId = Sentry.captureMessage(message)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureMessage(message: String, scopeCallback: (SentryScope) -> Unit): SentryId {
        val androidSentryId = Sentry.captureMessage(message, configureScopeCallback(scopeCallback))
        return SentryId(androidSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val androidSentryId = Sentry.captureException(throwable)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureException(throwable: Throwable, scopeCallback: (SentryScope) -> Unit): SentryId {
        val androidSentryId = Sentry.captureException(throwable, configureScopeCallback(scopeCallback))
        return SentryId(androidSentryId.toString())
    }

    actual fun configureScope(scopeCallback: (SentryScope) -> Unit) {
        Sentry.configureScope(configureScopeCallback(scopeCallback))
    }

    actual fun close() {
        Sentry.close()
    }

    private fun configureScopeCallback(scopeCallback: (SentryScope) -> Unit): (AndroidScope) -> Unit {
        return {
            val androidScopeImpl = SentryScopeAndroidImpl(it)
            val scope = SentryScope(androidScopeImpl)
            scopeCallback.invoke(scope)
        }
    }
}
