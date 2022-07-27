package io.sentry.kotlin.multiplatform

internal actual object SentryBridge {

    actual fun start(context: Any?, configuration: OptionsConfiguration<SentryKMPOptions>) {

    }

    actual fun captureMessage(message: String): SentryId {
        return SentryId("")
    }

    actual fun captureException(throwable: Throwable): SentryId {
        return SentryId("")
    }

    actual fun close() {

    }
}