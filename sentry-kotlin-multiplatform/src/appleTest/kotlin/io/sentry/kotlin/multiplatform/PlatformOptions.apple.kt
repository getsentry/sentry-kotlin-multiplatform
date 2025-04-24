package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaOptionsConfiguration
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlinx.cinterop.convert
import kotlin.test.assertEquals

actual interface PlatformOptions : CommonPlatformOptions {
    val enableWatchdogTerminationTracking: Boolean
}

open class SentryAppleOptionsWrapper(private val cocoaOptions: CocoaSentryOptions) :
    PlatformOptions {
    override val dsn: String?
        get() = cocoaOptions.dsn

    override val attachStackTrace: Boolean
        get() = cocoaOptions.attachStacktrace

    override val release: String?
        get() = cocoaOptions.releaseName

    override val debug: Boolean
        get() = cocoaOptions.debug

    override val environment: String
        get() = cocoaOptions.environment

    override val dist: String?
        get() = cocoaOptions.dist

    override val enableAutoSessionTracking: Boolean
        get() = cocoaOptions.enableAutoSessionTracking

    override val sessionTrackingIntervalMillis: Long
        get() = cocoaOptions.sessionTrackingIntervalMillis.convert()

    override val maxBreadcrumbs: Int
        get() = cocoaOptions.maxBreadcrumbs.convert()

    override val maxAttachmentSize: Long
        get() = cocoaOptions.maxAttachmentSize.convert()

    override val sampleRate: Double?
        get() = cocoaOptions.sampleRate?.doubleValue

    override val tracesSampleRate: Double?
        get() = cocoaOptions.tracesSampleRate?.doubleValue

    override val enableWatchdogTerminationTracking: Boolean
        get() = cocoaOptions.enableWatchdogTerminationTracking

    override val diagnosticLevel: SentryLevel
        get() = cocoaOptions.diagnosticLevel.toKmpSentryLevel()!!

    override val sendDefaultPii: Boolean
        get() = cocoaOptions.sendDefaultPii

    override fun applyFromOptions(options: SentryOptions) {
        options.toCocoaOptionsConfiguration().invoke(cocoaOptions)
    }
}

expect interface ApplePlatformOptions : PlatformOptions

actual fun createPlatformOptions(): PlatformOptions = createApplePlatformOptions()

expect fun createApplePlatformOptions(): PlatformOptions

expect fun ApplePlatformOptions.assertApplePlatformSpecificOptions(options: SentryOptions)

actual fun PlatformOptions.assertPlatformSpecificOptions(kmpOptions: SentryOptions) {
    (this as ApplePlatformOptions).assertApplePlatformSpecificOptions(kmpOptions)

    val appleOptions = this
    assertEquals(appleOptions.enableWatchdogTerminationTracking, kmpOptions.enableWatchdogTerminationTracking)
}

actual fun createSentryPlatformOptionsConfiguration(): PlatformOptionsConfiguration = {
    val cocoaOptions = it as CocoaSentryOptions
    cocoaOptions.dsn = fakeDsn
}
