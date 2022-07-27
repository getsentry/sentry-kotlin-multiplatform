package io.sentry.kotlin.multiplatform

internal expect object SentryBridge {
    /**
     * We can't use init because of `init` is reserved in Objective-C and Swift.
     */
    fun start(context: Any? = null, configuration: OptionsConfiguration<SentryKMPOptions>)

    fun captureMessage(message: String): SentryId

    fun captureException(throwable: Throwable): SentryId

    fun close()
}
