package io.sentry.kotlin.multiplatform

// TODO: Help needed!
internal actual object SentryBridge {
    actual fun start(dsn: String) {
    }

    actual fun captureMessage(msg: String) {
    }

    actual fun captureException(throwable: Throwable) {
    }

    actual fun close() {
    }
}