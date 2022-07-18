package io.sentry.kotlin.multiplatform

import io.sentry.Sentry as SentryJvm

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {
        val eventId = SentryJvm.captureMessage(message)
        return SentryId(eventId.toString())
    }

    actual fun start(context: Any?, configuration: OptionsConfiguration<SentryKMPOptions>) {

    }

    actual fun captureException(throwable: Throwable): SentryId {
        val eventId = SentryJvm.captureException(throwable)
        return SentryId(eventId.toString())
    }

    actual fun close() {
        SentryJvm.close()
    }

}