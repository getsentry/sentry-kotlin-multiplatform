package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
<<<<<<< Updated upstream:sentry-kotlin-multiplatform/src/appleMain/kotlin/io/sentry/kotlin/multiplatform/SentryApple.kt
import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryOptionsCallback
=======
import io.sentry.kotlin.multiplatform.extensions.toCocoaOptionsConfiguration
>>>>>>> Stashed changes:sentry-kotlin-multiplatform/src/commonAppleMain/kotlin/io/sentry/kotlin/multiplatform/SentryApple.kt
import io.sentry.kotlin.multiplatform.nsexception.asNSException
import io.sentry.kotlin.multiplatform.nsexception.setSentryUnhandledExceptionHook
import io.sentry.kotlin.multiplatform.protocol.SentryId
import platform.Foundation.NSError
import platform.Foundation.NSException

/** Convenience extension to setup unhandled exception hook */
internal fun SentrySDK.Companion.start(configuration: (CocoaSentryOptions?) -> Unit) {
    this.startWithConfigureOptions(configuration)
    setSentryUnhandledExceptionHook()
}

/**
 * Sentry initialization with an option configuration handler.
 *
 * @param configuration Options configuration handler.
 */
/*
fun Sentry.start(configuration: (SentryOptions) -> Unit) {
    val options = SentryOptions()
    configuration.invoke(options)
<<<<<<< Updated upstream:sentry-kotlin-multiplatform/src/appleMain/kotlin/io/sentry/kotlin/multiplatform/SentryApple.kt
    SentrySDK.startWithConfigureOptions(options.toCocoaSentryOptionsCallback())
    setSentryUnhandledExceptionHook()
=======
    SentrySDK.start(options.toCocoaOptionsConfiguration())
>>>>>>> Stashed changes:sentry-kotlin-multiplatform/src/commonAppleMain/kotlin/io/sentry/kotlin/multiplatform/SentryApple.kt
}
 */

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {

        val cocoaSentryId = SentrySDK.captureMessage(message)
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId {
        val cocoaSentryId = SentrySDK.captureMessage(message, configureScopeCallback(scopeCallback))
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val cocoaSentryId = SentrySDK.captureException(throwable.asNSException(true))
        return SentryId(cocoaSentryId.toString())
    }

    actual fun captureException(throwable: Throwable, scopeCallback: ScopeCallback): SentryId {
        val cocoaSentryId = SentrySDK.captureException(throwable.asNSException(true), configureScopeCallback(scopeCallback))
        return SentryId(cocoaSentryId.toString())
    }

    actual fun configureScope(scopeCallback: ScopeCallback) {
        SentrySDK.configureScope(configureScopeCallback(scopeCallback))
    }

    actual fun close() {
        SentrySDK.close()
    }

    private fun configureScopeCallback(scopeCallback: ScopeCallback): (CocoaScope?) -> Unit {
        return { cocoaScope ->
            val cocoaScopeImpl = cocoaScope?.let {
                ScopeCocoaImpl(it)
            }
            cocoaScopeImpl?.let {
                val scope = Scope(it)
                scopeCallback.invoke(scope)
            }
        }
    }
}

fun Sentry.captureError(error: NSError) {
    SentrySDK.captureError(error)
}

fun Sentry.captureException(exception: NSException) {
    SentrySDK.captureException(exception)
}
