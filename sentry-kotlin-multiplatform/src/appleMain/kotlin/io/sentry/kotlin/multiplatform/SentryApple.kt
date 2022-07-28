package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryEvent
import NSException.Sentry.SentryEvent as SentryNSExceptionEvent
import cocoapods.Sentry.SentryOptions
import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.nsexception.asNSException
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import platform.Foundation.NSError
import platform.Foundation.NSException

internal actual object SentryBridge {

    actual fun captureMessage(message: String) {
        SentrySDK.captureMessage(message)
    }

    actual fun captureException(throwable: Throwable) {
        SentrySDK.captureException(throwable.asNSException(true))
    }

    actual fun close() {
        SentrySDK.close()
    }

    internal fun convertToSentryAppleOptions(options: SentryKMPOptions): SentryOptions {
        val sentryAppleOptions = SentryOptions()
        sentryAppleOptions.dsn = options.dsn
        sentryAppleOptions.attachStacktrace = options.attachStackTrace
        sentryAppleOptions.beforeSend = { event ->
            dropKotlinCrashEvent(event as SentryNSExceptionEvent?) as SentryEvent?
        }
        return sentryAppleOptions
    }
}

fun Sentry.captureError(error: NSError) {
    SentrySDK.captureError(error)
}

fun Sentry.captureException(exception: NSException) {
    SentrySDK.captureException(exception)
}
