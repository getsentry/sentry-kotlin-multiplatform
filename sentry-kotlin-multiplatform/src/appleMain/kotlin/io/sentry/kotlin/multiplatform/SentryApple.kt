package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryOptions
import cocoapods.Sentry.SentrySDK
import platform.Foundation.*

internal actual object SentryBridge {
    actual fun start(context: Any?, configuration: OptionsConfiguration<SentryKMPOptions>) {
        val options = SentryKMPOptions()
        configuration.configure(options)
        SentrySDK.startWithOptionsObject(convertToSentryAppleOptions(options))
    }

    actual fun captureMessage(message: String): SentryId {
        val eventId = SentrySDK.captureMessage(message)
        return SentryId(eventId.sentryIdString)
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val exception = NSException("", throwable.message, null)
        val eventId = SentrySDK.captureException(exception)
        return SentryId(eventId.sentryIdString)
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
