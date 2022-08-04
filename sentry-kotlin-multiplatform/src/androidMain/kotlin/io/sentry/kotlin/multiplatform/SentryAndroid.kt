package io.sentry.kotlin.multiplatform

import android.content.Context
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryOptions
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.Sentry as SentryKmp

/**
 * Sentry initialization with a context and option configuration handler.
 *
 * @param context Application context.
 * @param configuration Options configuration handler.
 */
fun SentryKmp.init(context: Context, configuration: (SentryOptions) -> Unit) {
    val options = SentryOptions()
    configuration.invoke(options)
    SentryAndroid.init(context, options.toAndroidSentryOptions())
}

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {
        val androidSentryId = Sentry.captureMessage(message)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureMessage(message: String, scopeCallback: (Scope) -> Unit): SentryId {
        val androidSentryId = Sentry.captureMessage(message, configureScopeCallback(scopeCallback))
        return SentryId(androidSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val androidSentryId = Sentry.captureException(throwable)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureException(throwable: Throwable, scopeCallback: (Scope) -> Unit): SentryId {
        val androidSentryId = Sentry.captureException(throwable, configureScopeCallback(scopeCallback))
        return SentryId(androidSentryId.toString())
    }

    actual fun configureScope(scopeCallback: (Scope) -> Unit) {
        Sentry.configureScope(configureScopeCallback(scopeCallback))
    }

    actual fun close() {
        Sentry.close()
    }

    private fun configureScopeCallback(scopeCallback: (Scope) -> Unit): (AndroidScope) -> Unit {
        return {
            val androidScopeImpl = ScopeAndroidImpl(it)
            val scope = Scope(androidScopeImpl)
            scopeCallback.invoke(scope)
        }
    }
}
