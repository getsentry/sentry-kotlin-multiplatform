package io.sentry.kotlin.multiplatform

import android.content.Context
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryOptions

private val scope = SentryScope()

internal actual object SentryBridge {

    actual fun captureMessage(message: String) {
        Sentry.captureMessage(message)
    }

    actual fun start(context: Any?, configuration: OptionsConfiguration<SentryKMPOptions>) {
        val options = SentryKMPOptions()
        configuration.configure(options)
        if (context is Context) {
            SentryAndroid.init(context, options.toAndroidSentryOptions())
        }
    }

    actual fun captureException(throwable: Throwable) {
        Sentry.captureException(throwable)
    }

    actual fun configureScope(callback: SentryScopeCallback) {
        Sentry.configureScope {
            scope.initWithScope(it)
            callback.run(scope)
        }
    }

    actual fun close() {
        Sentry.close()
    }
}


