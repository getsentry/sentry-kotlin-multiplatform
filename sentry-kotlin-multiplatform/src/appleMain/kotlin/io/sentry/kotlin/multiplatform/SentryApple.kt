package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryOptions
import platform.Foundation.NSError
import platform.Foundation.NSException

private val scope = SentryScope()

internal actual object SentryBridge {
    
    actual fun start(context: Any?, configuration: OptionsConfiguration<SentryKMPOptions>) {
        val options = SentryKMPOptions()
        configuration.configure(options)
        SentrySDK.startWithOptionsObject(options.toCocoaSentryOptions())
    }

    actual fun captureMessage(message: String) {
        SentrySDK.captureMessage(message)
    }

    actual fun captureException(throwable: Throwable) {
        val exception = NSException(throwable::class.simpleName, throwable.message, null)
        SentrySDK.captureException(exception)
    }

    actual fun configureScope(callback: SentryScopeCallback) {
        callback.run(scope)
        SentrySDK.configureScope {
            scope.sentryLevel?.toCocoaSentryLevel()
            it?.setTags(scope.tags as Map<Any?, *>)
        }
    }

    actual fun close() {
        SentrySDK.close()
    }
}

fun SentryKMP.captureError(error: NSError) {
    SentrySDK.captureError(error)
}

fun SentryKMP.captureException(exception: NSException) {
    SentrySDK.captureException(exception)
}
