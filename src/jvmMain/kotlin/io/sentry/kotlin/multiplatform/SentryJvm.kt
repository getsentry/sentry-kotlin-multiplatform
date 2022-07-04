package io.sentry.kotlin.multiplatform

import io.sentry.Sentry as SentryJvm

internal actual object SentryBridge {

    actual fun captureMessage(message: String) {
        SentryJvm.captureMessage(message)
    }

    actual fun start(dsn: String, context: Any?) {
        SentryJvm.init {
            it.setDebug(true)
            it.dsn = dsn
        }
    }

    actual fun captureException(throwable: Throwable) {
        SentryJvm.captureException(throwable)
    }

    actual fun close() {
        SentryJvm.close()
    }

}
