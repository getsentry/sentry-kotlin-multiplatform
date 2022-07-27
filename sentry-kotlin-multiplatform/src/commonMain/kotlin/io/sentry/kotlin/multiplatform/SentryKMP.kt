package io.sentry.kotlin.multiplatform

/** Sentry Kotlin Multiplatform SDK API entry point */
object SentryKMP {

    /**
     * Sentry initialization with a context and option configuration handler.
     *
     * @param context Application context.
     * @param configuration Options configuration handler.
     */
    fun start(context: Any? = null, configuration: (SentryKMPOptions) -> Unit) {
        SentryBridge.start(context, configuration)
    }

    /**
     * Sentry initialization with an option configuration handler.
     *
     * @param configuration Options configuration handler.
     */
    fun start(configuration: (SentryKMPOptions) -> Unit) {
        SentryBridge.start(null, configuration)
    }

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