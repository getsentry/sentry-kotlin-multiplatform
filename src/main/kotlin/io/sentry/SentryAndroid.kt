package io.sentry

import android.content.Context
import io.sentry.android.core.SentryAndroid
import io.sentry.core.Sentry

internal actual object SentryBridge {

    actual fun captureMessage(msg: String) {
        Sentry.captureMessage(msg)
    }

    actual fun start(dsn: String) {
        SentryAndroid.init(ContextProvider.getContext()) { options ->
            options.dsn = dsn
            options.isAttachStacktrace = true
            options.isAttachThreads = true
        }
    }

    actual fun captureException(throwable: Throwable) {
        Sentry.captureException(throwable)
    }

    actual fun close() {
        Sentry.close()
    }
}

object ContextProvider {
    private lateinit var resolveContext: ()-> Context

    fun init(resolveContext: () -> Context) {
        this.resolveContext = resolveContext
    }

    fun getContext() : Context {
        return resolveContext()
    }
}


