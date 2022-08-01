package io.sentry.kotlin.multiplatform

/** Sentry Kotlin Multiplatform SDK API entry point */
object Sentry {

    /**
     * Captures the message.
     *
     * @param message The message to send.
     */
    fun captureMessage(message: String): SentryId {
        return SentryBridge.captureMessage(message)
    }

    /**
     * Captures the exception.
     *
     * @param message The message to send.
     * @param scopeCallback The local scope callback.
     */
    fun captureMessage(message: String, scopeCallback: (SentryScope) -> Unit): SentryId {
        return SentryBridge.captureMessage(message, scopeCallback)
    }

    /**
     * Captures the exception.
     *
     * @param throwable The exception.
     */
    fun captureException(throwable: Throwable): SentryId {
        return SentryBridge.captureException(throwable)
    }

    /**
     * Captures the exception.
     *
     * @param throwable The exception.
     * @param scopeCallback The local scope callback.
     */
    fun captureException(throwable: Throwable, scopeCallback: (SentryScope) -> Unit): SentryId {
        return SentryBridge.captureException(throwable, scopeCallback)
    }

        /**
     * Configures the scope through the callback.
     *
     * @param scopeCallback The configure scope callback.
     */
    fun configureScope(scopeCallback: (SentryScope) -> Unit) {
        SentryBridge.configureScope(scopeCallback)
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
