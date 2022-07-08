package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryOptions
import cocoapods.Sentry.SentrySDK
import platform.Foundation.NSException

internal actual object SentryBridge {
    actual fun start(context: Any?, configuration: OptionsConfiguration<SentryKMPOptions>) {
        val options = SentryKMPOptions()
        configuration.configure(options)
        SentrySDK.startWithOptionsObject(convertToSentryAppleOptions(options))
    }

    actual fun captureMessage(message: String) {
        SentrySDK.captureMessage(message)
    }

    actual fun captureException(throwable: Throwable) {
        val exception = NSException("", throwable.message, null)
        SentrySDK.captureException(exception)
    }

    actual fun close() {
        SentrySDK.close()
    }

    private fun convertToSentryAppleOptions(options: SentryKMPOptions): SentryOptions {
        val sentryAppleOptions = SentryOptions()
        sentryAppleOptions.dsn = options.dsn
        sentryAppleOptions.attachStacktrace = options.attachStackTrace
        return sentryAppleOptions
    }
}
