package io.sentry.kotlin.multiplatform

import android.content.Context
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid

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
        ContextProvider.resolveContext = resolveContext
    }

    fun getContext() : Context {
        return resolveContext()
    }
}


