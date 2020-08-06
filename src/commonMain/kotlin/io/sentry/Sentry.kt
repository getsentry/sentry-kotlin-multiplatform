package io.sentry

object Sentry {
    fun init(dsn: String) {
        SentryBridge.start(dsn)
    }

    fun captureMessage(msg: String) {
        SentryBridge.captureMessage(msg)
    }

    fun captureException(throwable: Throwable) {
        SentryBridge.captureException(throwable)
    }

    /**
     * Throws a RuntimeException, useful for testing.
     */
    fun crash() {
        throw RuntimeException("Uncaught Exception from Kotlin Multiplatform.")
    }

    fun close() {
        SentryBridge.close()
    }
}

internal expect object SentryBridge {
    /**
     * We can't use init because of `init` is reserved in Objective-C and Swift.
     */
    fun start(dsn: String)

    fun captureMessage(msg: String)

    fun captureException(throwable: Throwable)

    fun close()
}
