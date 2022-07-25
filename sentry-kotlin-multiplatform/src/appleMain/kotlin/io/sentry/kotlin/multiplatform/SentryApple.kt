package io.sentry.kotlin.multiplatform

import Sentry.SentryEvent
import cocoapods.Sentry.SentryOptions
import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.core.dropKotlinCrashEvent
import io.sentry.kotlin.multiplatform.core.setSentryUnhandledExceptionHook
import platform.Foundation.NSError
import platform.Foundation.NSException

internal actual object SentryBridge {
    actual fun start(context: Any?, configuration: OptionsConfiguration<SentryKMPOptions>) {
        val options = SentryKMPOptions()
        configuration.configure(options)
        SentrySDK.startWithOptionsObject(convertToSentryAppleOptions(options))
        setSentryUnhandledExceptionHook()
    }

    actual fun captureMessage(message: String) {
        SentrySDK.captureMessage(message)
    }

    actual fun captureException(throwable: Throwable) {
        val exception = NSException(throwable::class.simpleName, throwable.message, null)
        SentrySDK.captureException(exception)
    }

    actual fun close() {
        SentrySDK.close()
    }

    private fun convertToSentryAppleOptions(options: SentryKMPOptions): SentryOptions {
        val sentryAppleOptions = SentryOptions()
        sentryAppleOptions.dsn = options.dsn
        sentryAppleOptions.attachStacktrace = options.attachStackTrace
        sentryAppleOptions.beforeSend = { event ->
            dropKotlinCrashEvent(event as SentryEvent?) as cocoapods.Sentry.SentryEvent?
        }
        return sentryAppleOptions
    }
}

fun SentryKMP.captureError(error: NSError) {
    SentrySDK.captureError(error)
}

fun SentryKMP.captureException(exception: NSException) {
    SentrySDK.captureException(exception)
}