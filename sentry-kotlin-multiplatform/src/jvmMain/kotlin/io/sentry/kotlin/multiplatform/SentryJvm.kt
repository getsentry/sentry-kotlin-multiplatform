package io.sentry.kotlin.multiplatform

import io.sentry.Sentry as SentryJvm

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {
        val javaSentryId = SentryJvm.captureMessage(message)
        return SentryId(javaSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val javaSentryId = SentryJvm.captureException(throwable)
        return SentryId(javaSentryId.toString())
    }

    actual fun configureScope(callback: SentryScopeCallback) {

    }

    actual fun close() {
        SentryJvm.close()
    }
}
