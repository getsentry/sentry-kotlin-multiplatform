package io.sentry.kotlin.multiplatform

import android.content.Context
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid
import io.sentry.android.core.SentryAndroidOptions

internal actual object SentryBridge {
    actual fun start(context: Any?, configuration: OptionsConfiguration<SentryKMPOptions>) {
        val options = SentryKMPOptions()
        configuration.configure(options)
        if (context is Context) {
            SentryAndroid.init(context, convertToSentryAndroidOptions(options))
        }
    }

    actual fun captureMessage(message: String): SentryId {
        val eventId = Sentry.captureMessage(message)
        return SentryId(eventId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val eventId = Sentry.captureException(throwable)
        return SentryId(eventId.toString())
    }

    actual fun close() {
        Sentry.close()
    }

    private fun convertToSentryAndroidOptions(options: SentryKMPOptions): (SentryAndroidOptions) -> Unit {
        return { sentryAndroidOptions ->
            sentryAndroidOptions.dsn = options.dsn
            sentryAndroidOptions.isAttachThreads = options.attachThreads
            sentryAndroidOptions.isAttachStacktrace = options.attachStackTrace
            sentryAndroidOptions.isDebug = options.debug
        }
    }
}
