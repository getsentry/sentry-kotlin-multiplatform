package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryOptions
import cocoapods.Sentry.SentrySDK
import platform.Foundation.NSError
import platform.Foundation.NSException

private val scope = SentryScope()

internal actual object SentryBridge {

    actual fun captureMessage(message: String) {
        SentrySDK.captureMessage(message)
    }

    actual fun captureException(throwable: Throwable) {
        val exception = NSException(throwable::class.simpleName, throwable.message, null)
        SentrySDK.captureException(exception)
    }

    actual fun configureScope(callback: SentryScopeCallback) {
        SentrySDK.configureScope {
            scope.initWithScope(it!!)
            callback.run(scope)
        }
    }

    actual fun close() {
        SentrySDK.close()
    }

    internal fun convertToSentryAppleOptions(options: SentryKMPOptions): SentryOptions {
        val sentryAppleOptions = SentryOptions()
        sentryAppleOptions.dsn = options.dsn
        sentryAppleOptions.attachStacktrace = options.attachStackTrace
        return sentryAppleOptions
    }
}

fun Sentry.captureError(error: NSError) {
    SentrySDK.captureError(error)
}

fun Sentry.captureException(exception: NSException) {
    SentrySDK.captureException(exception)
}
