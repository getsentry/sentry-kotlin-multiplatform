package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId

typealias ScopeCallback = (Scope) -> Unit
/*
class OptionsConfiguration {
    var dsn: String? = null

    private var androidOptions = AndroidOptions()
    private var cocoaOptions = CocoaOptions()

    fun android(config: AndroidOptions.() -> Unit) {
        config.invoke(androidOptions)
    }

    fun ios(config: CocoaOptions.() -> Unit) {
        config.invoke(cocoaOptions)
    }

    class AndroidOptions() {
        var attach = false
        var anrEnabled = true
    }

    class CocoaOptions {
        var attachScreenshot = false
    }
}

inline fun Sentry.init(con: OptionsConfiguration.() -> Unit) {
    val options = OptionsConfiguration()
    con.invoke(options)
}

fun foo() {
    Sentry.init {
        dsn = "__dsn__"

        android {
            anrEnabled = true
        }
        ios {
            attachScreenshot = true
        }
    }
}

 */

/** Sentry Kotlin Multiplatform SDK API entry point */
object Sentry {

    /**
     * Captures the message.
     *
     * @param message The message to send.
     */
    fun captureMessage(message: String): SentryId {
        return SentryBridge.captureMessage(message)
    }

    /**
     * Captures the exception.
     *
     * @param message The message to send.
     * @param scopeCallback The local scope callback.
     */
    fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId {
        return SentryBridge.captureMessage(message, scopeCallback)
    }

    /**
     * Captures the exception.
     *
     * @param throwable The exception.
     */
    fun captureException(throwable: Throwable): SentryId {
        return SentryBridge.captureException(throwable)
    }

    /**
     * Captures the exception.
     *
     * @param throwable The exception.
     * @param scopeCallback The local scope callback.
     */
    fun captureException(throwable: Throwable, scopeCallback: ScopeCallback): SentryId {
        return SentryBridge.captureException(throwable, scopeCallback)
    }

        /**
     * Configures the scope through the callback.
     *
     * @param scopeCallback The configure scope callback.
     */
    fun configureScope(scopeCallback: ScopeCallback) {
        SentryBridge.configureScope(scopeCallback)
    }

    /**
     * Throws a RuntimeException, useful for testing.
     */
    fun crash() {
        throw RuntimeException("Uncaught Exception from Kotlin Multiplatform.")
    }

    /**
     * Closes the SDK.
     */
    fun close() {
        SentryBridge.close()
    }
}
