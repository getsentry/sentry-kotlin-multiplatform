package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.nsexception.asNSException
import platform.Foundation.NSError
import platform.Foundation.NSException

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {
        val cocoaSentryId = SentrySDK.captureMessage(message)
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureMessage(message: String, scopeCallback: (SentryScope) -> Unit): SentryId {
        val scope = SentryScope()
        val scopeConfiguration = scope.scopeConfiguration(scopeCallback)
        val cocoaSentryId = SentrySDK.captureMessage(message, scopeConfiguration)
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val cocoaSentryId = SentrySDK.captureException(throwable.asNSException(true))
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureException(throwable: Throwable, scopeCallback: (SentryScope) -> Unit): SentryId {
        val scope = SentryScope()
        val scopeConfiguration = scope.scopeConfiguration(scopeCallback)
        val cocoaSentryId = SentrySDK.captureException(throwable.asNSException(true), scopeConfiguration)
        return SentryId(cocoaSentryId.toString())
    }

    actual fun configureScope(scopeCallback: (SentryScope) -> Unit) {
        val scope = SentryScope()
        val scopeConfiguration = scope.scopeConfiguration(scopeCallback)
        SentrySDK.configureScope(scopeConfiguration)
    }

    actual fun close() {
        SentrySDK.close()
    }
}

fun Sentry.captureError(error: NSError) {
    SentrySDK.captureError(error)
}

fun Sentry.captureException(exception: NSException) {
    SentrySDK.captureException(exception)
}
