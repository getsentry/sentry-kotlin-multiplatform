package io.sentry.kotlin.multiplatform

import io.sentry.Sentry
import io.sentry.android.core.SentryAndroidOptions

private val scope = SentryScope()

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {
        val androidSentryId = Sentry.captureMessage(message)
        return SentryId(androidSentryId.toString())
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val androidSentryId = Sentry.captureException(throwable)
        return SentryId(androidSentryId.toString())
    }

    actual fun configureScope(callback: SentryScopeCallback) {
        Sentry.configureScope {
            scope.initWithScope(it)
            callback.run(scope)
        }
    }

    actual fun close() {
        Sentry.close()
    }
}
