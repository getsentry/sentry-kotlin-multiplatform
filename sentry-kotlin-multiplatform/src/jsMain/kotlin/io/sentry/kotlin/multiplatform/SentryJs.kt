package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId

@JsModule("@sentry/browser")
@JsNonModule
@JsName("Sentry")
external object SentryJs {

    fun init(options: dynamic)

    fun captureMessage(message: String): dynamic

    fun captureMessage(message: String, captureContext: (Scope: dynamic) -> Unit): dynamic

    fun captureException(exception: Any): dynamic

    fun captureException(exception: Any, captureContext: (Scope: dynamic) -> Unit): dynamic

    fun configureScope(callback: (Scope: dynamic) -> Unit)

    fun close()
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
        val jsSentryId = SentryJs.captureMessage(message)
        return SentryId(jsSentryId)
    }

    actual fun captureMessage(message: String, scopeCallback: (Scope) -> Unit): SentryId {
        val jsSentryId = SentryJs.captureMessage(message) {
            val scopeJsImpl = ScopeJsImpl(it)
            val scope = Scope(scopeJsImpl)
            scopeCallback.invoke(scope)
        }
        return SentryId(jsSentryId)
    }

    actual fun captureException(throwable: Throwable): SentryId {
        val jsSentryId = SentryJs.captureException(throwable)
        return SentryId(jsSentryId)
    }

    actual fun captureException(throwable: Throwable, scopeCallback: (Scope) -> Unit): SentryId {
        val jsSentryId = SentryJs.captureException(throwable) {
            val scopeJsImpl = ScopeJsImpl(it)
            val scope = Scope(scopeJsImpl)
            scopeCallback.invoke(scope)
        }
        return SentryId(jsSentryId)
    }

    actual fun configureScope(scopeCallback: (Scope) -> Unit) {
        SentryJs.configureScope {
            val scopeJsImpl = ScopeJsImpl(it)
            val scope = Scope(scopeJsImpl)
            scopeCallback.invoke(scope)
        }
    }

    actual fun close() {
        SentryJs.close()
    }
}
