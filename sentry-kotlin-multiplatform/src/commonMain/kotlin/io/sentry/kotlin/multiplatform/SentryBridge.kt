package io.sentry.kotlin.multiplatform

internal expect object SentryBridge {
    fun captureMessage(message: String)

    fun captureException(throwable: Throwable)

    fun close()
}
