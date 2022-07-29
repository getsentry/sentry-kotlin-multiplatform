package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.nsexception.asNSException
import platform.Foundation.NSError
import platform.Foundation.NSException

private val scope = SentryScope()

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {
        val cocoaSentryId = SentrySDK.captureMessage(message)
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val cocoaSentryId = SentrySDK.captureException(throwable.asNSException(true))
        return SentryId(cocoaSentryId.toString())
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
}

fun Sentry.captureError(error: NSError) {
    SentrySDK.captureError(error)
}

fun Sentry.captureException(exception: NSException) {
    SentrySDK.captureException(exception)
}
