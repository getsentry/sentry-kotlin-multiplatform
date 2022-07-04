package io.sentry.kotlin.multiplatform

import android.content.Context
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid

internal actual object SentryBridge {

    actual fun captureMessage(message: String) {
        Sentry.captureMessage(message)
    }

    actual fun start(dsn: String, context: Any?) {
        SentryAndroid.init(context as Context) { options ->
            options.dsn = dsn
            options.isAttachStacktrace = true
            options.isAttachThreads = true
        }
    }

    actual fun captureException(throwable: Throwable) {
        Sentry.captureException(throwable)
    }

    actual fun close() {
        Sentry.close()
    }
}


