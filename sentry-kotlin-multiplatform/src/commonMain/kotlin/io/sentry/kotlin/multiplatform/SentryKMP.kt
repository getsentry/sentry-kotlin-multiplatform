package io.sentry.kotlin.multiplatform

/** Sentry Kotlin Multiplatform SDK API entry point */
object Sentry {
    /**
     * Captures the message.
     *
     * @param message The message to send.
     */
    fun captureMessage(message: String) {
        SentryBridge.captureMessage(message)
    }

    /**
     * Captures the exception.
     *
     * @param throwable The exception.
     */
    fun captureException(throwable: Throwable) {
        SentryBridge.captureException(throwable)
    }

    /**
     * Configures the scope through the callback.
     *
     * @param callback The configure scope callback.
     */
    fun configureScope(callback: (SentryScope) -> Unit) {
        SentryBridge.configureScope(callback)
    }

    /**
     * Throws a RuntimeException, useful for testing.
     */
    fun crash() {
        throw RuntimeException("Uncaught Exception from Kotlin Multiplatform.")
    }

    /**
     * Closes the SDK.
     */
    fun close() {
        SentryBridge.close()
    }
}
