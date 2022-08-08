package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId

@JsModule("@sentry/browser")
@JsNonModule
@JsName("Sentry")
external object SentryJs {
    interface BrowserOptions

    interface Scope

    fun init(options: BrowserOptions = definedExternally)

    fun captureMessage(message: String)

    fun captureException(exception: Any)

    fun configureScope(callback: (Scope: dynamic) -> Unit)
}

fun Sentry.init(configuration: (SentryOptions) -> Unit) {
    val options = SentryOptions()
    configuration.invoke(options)
    val jsOptions: dynamic = Any()
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
        SentryJs.configureScope {
            val scopeJsImpl = ScopeJsImpl(it)
            val scope = Scope(scopeJsImpl)
            scopeCallback.invoke(scope)
        }
    }

    actual fun close() {

    }
}
