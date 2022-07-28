package io.sentry.kotlin.multiplatform

import io.sentry.Sentry
import io.sentry.android.core.SentryAndroidOptions

internal actual object SentryBridge {
    actual fun captureMessage(message: String) {
        Sentry.captureMessage(message)
    }

    actual fun captureException(throwable: Throwable) {
        Sentry.captureException(throwable)
    }

    actual fun close() {
        Sentry.close()
    }

    internal fun convertToSentryAndroidOptions(options: SentryKMPOptions): (SentryAndroidOptions) -> Unit {
        return { sentryAndroidOptions ->
            sentryAndroidOptions.dsn = options.dsn
            sentryAndroidOptions.isAttachThreads = options.attachThreads
            sentryAndroidOptions.isAttachStacktrace = options.attachStackTrace
        }
    }
}

