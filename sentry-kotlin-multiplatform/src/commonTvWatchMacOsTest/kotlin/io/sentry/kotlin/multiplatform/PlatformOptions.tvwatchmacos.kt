package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaOptionsConfiguration
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlinx.cinterop.convert
import kotlin.test.assertEquals

actual interface PlatformOptions : CommonPlatformOptions

class SentryTvWatchMacOsOptionsWrapper(private val cocoaOptions: CocoaSentryOptions) :
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

    override fun applyFromOptions(options: SentryOptions) {
        options.toCocoaOptionsConfiguration().invoke(cocoaOptions)
    }
}

actual fun createPlatformOptions(): PlatformOptions =
    SentryTvWatchMacOsOptionsWrapper(CocoaSentryOptions())

actual fun PlatformOptions.assertPlatformSpecificOptions(options: SentryOptions) {
    // no-op
}

actual class SentryPlatformOptionsFoo {
    private var event: CocoaSentryEvent? = null

    actual fun init() {
        Sentry.initWithPlatformOptions {
            it.dsn = fakeDsn
            it.setBeforeSend { event ->
                this.event = event
                event
            }
        }

        // Trigger beforeSend
        Sentry.captureMessage("Test Message")
    }

    actual fun assertSdkNameAndVersion() {
        val sdk = event?.sdk
        assertEquals(BuildKonfig.SENTRY_KMP_COCOA_SDK_NAME, sdk!!["name"])
        assertEquals(BuildKonfig.VERSION_NAME, sdk["version"])
    }
}
