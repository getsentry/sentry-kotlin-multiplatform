package io.sentry.kotlin.multiplatform

import platform.Foundation.*
import cocoapods.Sentry.*

internal actual object SentryBridge {
    actual fun start(dsn: String) {
        SentrySDK.startWithConfigureOptions {
            it!!.dsn = dsn
            it.debug = true
        }
    }

    actual fun captureMessage(msg: String) {
        SentrySDK.captureMessage(msg)
    }

    actual fun captureException(throwable: Throwable) {
        // The reason is of the NSException is used for the message of the event
        SentrySDK.captureException(NSException("", throwable.message, null))
    }

    actual fun close() {
        // sentry-cocoa has no close
    }
}
