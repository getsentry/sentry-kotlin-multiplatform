package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId

external interface BrowserOptions

class BrowserOptionsImpl: BrowserOptions


@JsModule("@sentry/browser")
@JsNonModule
@JsName("Sentry")
external object SentryJs {
    interface BrowserOptions

    fun init(options: BrowserOptions = definedExternally)

    fun captureMessage(message: String)

    fun captureException(exception: Any)
}

fun Sentry.init(configuration: (SentryOptions) -> Unit) {
    val options = SentryOptions()
    configuration.invoke(options)
    val jsOptions: dynamic = BrowserOptionsImpl()
    jsOptions.dsn = options.dsn
    SentryJs.init(jsOptions)
}

internal actual object SentryBridge {

    actual fun captureMessage(message: String): SentryId {
        SentryJs.captureMessage(message)
        return SentryId.EMPTY_ID
    }

    actual fun captureMessage(
        message: String,
        scopeCallback: (Scope) -> Unit
    ): SentryId {
        return SentryId.EMPTY_ID
    }

    actual fun captureException(throwable: Throwable): SentryId {
        SentryJs.captureException(throwable)
        return SentryId.EMPTY_ID
    }

    actual fun captureException(
        throwable: Throwable,
        scopeCallback: (Scope) -> Unit
    ): SentryId {
        return SentryId.EMPTY_ID
    }

    actual fun configureScope(scopeCallback: (Scope) -> Unit) {
    }

    actual fun close() {
    }
}
