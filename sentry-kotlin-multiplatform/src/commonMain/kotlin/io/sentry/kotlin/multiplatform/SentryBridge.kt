package io.sentry.kotlin.multiplatform

internal expect object SentryBridge {
    /**
     * We can't use init because of `init` is reserved in Objective-C and Swift.
     */
    fun start(context: Any? = null, configuration: OptionsConfiguration<SentryKMPOptions>)

    fun captureMessage(message: String)

    fun captureException(throwable: Throwable)

    fun configureScope(callback: SentryScopeCallback)

    fun close()
}
