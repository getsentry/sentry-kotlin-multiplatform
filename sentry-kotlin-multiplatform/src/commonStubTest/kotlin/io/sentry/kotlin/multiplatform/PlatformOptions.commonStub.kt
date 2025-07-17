package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.utils.fakeDsn

actual interface PlatformOptions : CommonPlatformOptions

private class StubPlatformOptionsImpl : PlatformOptions {
    override var dsn: String? = null
    override var attachStackTrace: Boolean = true
    override var release: String? = null
    override var debug: Boolean = false
    override var environment: String? = null
    override var dist: String? = null
    override var enableAutoSessionTracking: Boolean = true
    override var sessionTrackingIntervalMillis: Long = DEFAULT_SESSION_INTERVAL_MILLIS
    override var maxBreadcrumbs: Int = DEFAULT_MAX_BREADCRUMBS
    override var maxAttachmentSize: Long = DEFAULT_MAX_ATTACHMENT_SIZE
    override var sampleRate: Double? = null
    override var tracesSampleRate: Double? = null
    override var diagnosticLevel: SentryLevel = SentryLevel.DEBUG
    override var sendDefaultPii: Boolean = false

    override fun applyFromOptions(options: SentryOptions) {
        dsn = options.dsn
        attachStackTrace = options.attachStackTrace
        release = options.release
        debug = options.debug
        environment = options.environment
        dist = options.dist
        enableAutoSessionTracking = options.enableAutoSessionTracking
        sessionTrackingIntervalMillis = options.sessionTrackingIntervalMillis
        maxBreadcrumbs = options.maxBreadcrumbs
        maxAttachmentSize = options.maxAttachmentSize
        sampleRate = options.sampleRate
        tracesSampleRate = options.tracesSampleRate
        diagnosticLevel = options.diagnosticLevel
        sendDefaultPii = options.sendDefaultPii
    }
}

actual fun createPlatformOptions(): PlatformOptions = StubPlatformOptionsImpl()

actual fun createSentryPlatformOptionsConfiguration(): PlatformOptionsConfiguration = {
}

actual fun PlatformOptions.assertPlatformSpecificOptions(kmpOptions: SentryOptions) { }