package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryEvent
import NSException.Sentry.SentryEvent as SentryNSExceptionEvent
import cocoapods.Sentry.SentryOptions
import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.nsexception.asNSException
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import io.sentry.kotlin.multiplatform.nsexception.setSentryUnhandledExceptionHook
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
        SentrySDK.captureException(throwable.asNSException(true))
    }

    actual fun close() {
        SentrySDK.close()
    }

    private fun convertToSentryAppleOptions(options: SentryKMPOptions): SentryOptions {
        val sentryAppleOptions = SentryOptions()
        sentryAppleOptions.dsn = options.dsn
        sentryAppleOptions.debug = true
        sentryAppleOptions.attachStacktrace = options.attachStackTrace
        sentryAppleOptions.beforeSend = { event ->
            dropKotlinCrashEvent(event as SentryNSExceptionEvent?) as SentryEvent?
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