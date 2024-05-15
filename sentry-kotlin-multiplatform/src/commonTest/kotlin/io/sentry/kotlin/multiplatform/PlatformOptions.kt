package io.sentry.kotlin.multiplatform

interface CommonPlatformOptions {
    val dsn: String?
    val attachStackTrace: Boolean
    val release: String?
    val debug: Boolean
    val environment: String?
    val dist: String?
    val enableAutoSessionTracking: Boolean
    val sessionTrackingIntervalMillis: Long
    val maxBreadcrumbs: Int
    val maxAttachmentSize: Long
    val sampleRate: Double?
    val tracesSampleRate: Double?

    fun applyFromOptions(options: SentryOptions)
}

expect interface PlatformOptions : CommonPlatformOptions

expect fun createPlatformOptions(): PlatformOptions

expect fun createSentryPlatformOptionsConfiguration(): PlatformOptionsConfiguration

/**
 * This function is only used in tests to convert platform options to assert some properties.
 */
expect fun SentryPlatformOptions.toSentryOptions(): SentryOptions
