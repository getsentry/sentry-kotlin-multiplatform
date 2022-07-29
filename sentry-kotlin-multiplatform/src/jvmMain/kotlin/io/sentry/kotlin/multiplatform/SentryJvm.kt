package io.sentry.kotlin.multiplatform

import io.sentry.Sentry as SentryJvm

internal actual object SentryBridge {

    actual fun captureMessage(message: String) {
        SentryJvm.captureMessage(message)
    }

    actual fun captureException(throwable: Throwable) {
        SentryJvm.captureException(throwable)
    }

    actual fun configureScope(callback: SentryScopeCallback) {

    }

    actual fun close() {
        SentryJvm.close()
    }

}
