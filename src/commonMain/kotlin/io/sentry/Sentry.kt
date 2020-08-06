package io.sentry

object Sentry {
    fun start(dsn: String) {
        SentryDelegate.start(dsn)
    }

    fun captureMessage(msg: String) {
        SentryDelegate.captureMessage(msg)
    }

    fun captureException(throwable: Throwable) {
        SentryDelegate.captureException(throwable)
    }

    /**
     * Throws a RuntimeException, useful for testing.
     */
    fun crash() {
        throw RuntimeException("Uncaught Exception from Kotlin Multiplatform.")
    }

    fun close() {
        SentryDelegate.close()
    }
}

internal expect object SentryDelegate {
    fun start(dsn: String)

    fun captureMessage(msg: String)

    fun captureException(throwable: Throwable)

    fun close()
}
