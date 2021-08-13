package io.sentry.kotlin.multiplatform

import android.content.Context
import androidx.startup.Initializer
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid

private var appContext: Context? = null

internal actual object SentryBridge {

    actual fun captureMessage(msg: String) {
        Sentry.captureMessage(msg)
    }

    actual fun start(dsn: String) {
        val appContext = appContext ?: error("SentryBridge.start is called too early")
        SentryAndroid.init(appContext) { options ->
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

// TODO: probably this shouldn't be the default behavior, we could have it as an option, e.g. to have it as a different Gradle module
// TODO: the default would be that the users have to provide Context explicitly
internal class SentryInitializer : Initializer<Context> {
    override fun create(context: Context): Context = context.applicationContext.also { appContext = it }
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
