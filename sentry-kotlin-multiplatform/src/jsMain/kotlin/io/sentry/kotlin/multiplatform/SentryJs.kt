package io.sentry.kotlin.multiplatform

internal actual object SentryBridge {

    actual fun start(context: Any?, configuration: OptionsConfiguration<SentryKMPOptions>) {

    }

    actual fun captureMessage(message: String): SentryId {
        return SentryId.EMPTY_ID
    }

    actual fun captureException(throwable: Throwable): SentryId {
        return SentryId.EMPTY_ID
    }

    actual fun close() {

    }
}
