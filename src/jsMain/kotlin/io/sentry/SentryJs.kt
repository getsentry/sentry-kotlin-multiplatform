package io.sentry

internal actual object SentryDelegate {
    actual fun start(dsn: String) {
    }

    actual fun captureMessage(msg: String) {
    }

    actual fun captureException(throwable: Throwable) {
    }

    actual fun close() {
    }
}