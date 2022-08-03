package io.sentry.kotlin.multiplatform

import android.content.Context
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryOptions
import io.sentry.kotlin.multiplatform.protocol.SentryId

/**
 * Sentry initialization with a context and option configuration handler.
 *
 * @param context Application context.
 * @param configuration Options configuration handler.
 */
fun Sentry.init(context: Context, configuration: (SentryOptions) -> Unit) {
    val options = SentryOptions()
    configuration.invoke(options)
    SentryAndroid.init(context, options.toAndroidSentryOptions())
}

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
