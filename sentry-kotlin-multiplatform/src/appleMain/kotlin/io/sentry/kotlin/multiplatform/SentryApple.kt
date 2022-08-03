package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.nsexception.asNSException
import io.sentry.kotlin.multiplatform.protocol.SentryId
import platform.Foundation.NSError
import platform.Foundation.NSException

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {
        val cocoaSentryId = SentrySDK.captureMessage(message)
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureMessage(message: String, scopeCallback: (SentryScope) -> Unit): SentryId {
        val cocoaSentryId = SentrySDK.captureMessage(message, configureScopeCallback(scopeCallback))
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val cocoaSentryId = SentrySDK.captureException(throwable.asNSException(true))
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureException(throwable: Throwable, scopeCallback: (SentryScope) -> Unit): SentryId {
        val cocoaSentryId = SentrySDK.captureException(throwable.asNSException(true), configureScopeCallback(scopeCallback))
        return SentryId(cocoaSentryId.toString())
    }

    actual fun configureScope(scopeCallback: (SentryScope) -> Unit) {
        SentrySDK.configureScope(configureScopeCallback(scopeCallback))
    }

    actual fun close() {
        SentrySDK.close()
    }

    private fun configureScopeCallback(scopeCallback: (SentryScope) -> Unit): (CocoaScope?) -> Unit {
        return { cocoaScope ->
            val cocoaScopeImpl = cocoaScope?.let {
                SentryScopeCocoaImpl(it)
            }  
            val scope = cocoaScopeImpl?.let { SentryScope(it) }
            scope?.let { scopeCallback.invoke(it) }
        }
    }
}

fun Sentry.captureError(error: NSError) {
    SentrySDK.captureError(error)
}

fun Sentry.captureException(exception: NSException) {
    SentrySDK.captureException(exception)
}
