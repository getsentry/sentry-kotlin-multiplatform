package io.sentry.kotlin.multiplatform

import io.sentry.Sentry as SentryJvm

internal actual object SentryBridge {

    actual fun captureMessage(msg: String) {
        SentryJvm.captureMessage(msg)
    }

    actual fun start(dsn: String) {
        SentryJvm.init() {
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
