package io.sentry.kotlin.multiplatform

internal expect object SentryBridge {

    fun captureMessage(message: String): SentryId

    fun captureException(throwable: Throwable): SentryId

    fun close()
}
