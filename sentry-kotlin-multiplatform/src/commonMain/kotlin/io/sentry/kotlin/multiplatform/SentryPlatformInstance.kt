package io.sentry.kotlin.multiplatform

internal interface SentryInstance {
    fun init(configuration: PlatformOptionsConfiguration)
}

/**
 * Represents the actual Sentry SDK instance.
 */
internal expect class SentryPlatformInstance() : SentryInstance {
    override fun init(configuration: PlatformOptionsConfiguration)
}
