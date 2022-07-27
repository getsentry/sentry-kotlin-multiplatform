package io.sentry.kotlin.multiplatform

import android.content.Context
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid
import io.sentry.android.core.SentryAndroidOptions

internal actual object SentryBridge {
    actual fun captureMessage(message: String) {
        Sentry.captureMessage(message)
    }

    actual fun start(context: Any?, configuration: OptionsConfiguration<SentryKMPOptions>) {
        val options = SentryKMPOptions()
        configuration.configure(options)
        if (context is Context) {
            SentryAndroid.init(context, convertToSentryAndroidOptions(options))
        }
        Sentry.configureScope {
            it.transaction = null
        }
    }

    actual fun captureException(throwable: Throwable) {
        Sentry.captureException(throwable)
    }

    actual fun close() {
        Sentry.close()
    }

    private fun convertToSentryAndroidOptions(options: SentryKMPOptions): (SentryAndroidOptions) -> Unit {
        return { sentryAndroidOptions ->
            sentryAndroidOptions.dsn = options.dsn
            sentryAndroidOptions.isAttachThreads = options.attachThreads
            sentryAndroidOptions.isAttachStacktrace = options.attachStackTrace
        }
    }
}


