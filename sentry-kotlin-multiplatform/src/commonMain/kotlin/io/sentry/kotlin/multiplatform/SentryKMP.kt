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
    fun captureMessage(message: String): SentryId {
        return SentryBridge.captureMessage(message)
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

fun interface OptionsConfiguration<T: SentryKMPOptions> {
    fun configure(options: T)
}