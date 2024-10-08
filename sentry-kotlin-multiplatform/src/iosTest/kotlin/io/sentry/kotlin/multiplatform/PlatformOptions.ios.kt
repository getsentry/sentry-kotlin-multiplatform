package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryReplayOptions
import io.sentry.kotlin.multiplatform.extensions.toIosOptionsConfiguration
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlinx.cinterop.convert
import kotlin.test.assertEquals

actual interface PlatformOptions : CommonPlatformOptions {
    val attachScreenshot: Boolean
    val attachViewHierarchy: Boolean
    val enableAppHangTracking: Boolean
    val appHangTimeoutIntervalMillis: Long
    val sessionReplay: SentryReplayOptions
}

class SentryIosOptionsWrapper(private val cocoaOptions: CocoaSentryOptions) : PlatformOptions {
    override val attachScreenshot: Boolean
        get() = cocoaOptions.attachScreenshot

    override val attachViewHierarchy: Boolean
        get() = cocoaOptions.attachViewHierarchy

    override val enableAppHangTracking: Boolean
        get() = cocoaOptions.enableAppHangTracking

    override val appHangTimeoutIntervalMillis: Long
        get() = cocoaOptions.appHangTimeoutInterval.toLong()

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

    override val sessionReplay: SentryReplayOptions
        get() = cocoaOptions.experimental.sessionReplay()

    override fun applyFromOptions(options: SentryOptions) {
        options.toIosOptionsConfiguration().invoke(cocoaOptions)
    }
}

actual fun createPlatformOptions(): PlatformOptions = SentryIosOptionsWrapper(CocoaSentryOptions())

actual fun PlatformOptions.assertPlatformSpecificOptions(options: SentryOptions) {
    assertEquals(attachScreenshot, options.attachScreenshot)
    assertEquals(attachViewHierarchy, options.attachViewHierarchy)
    assertEquals(enableAppHangTracking, options.enableAppHangTracking)
    assertEquals(appHangTimeoutIntervalMillis, options.appHangTimeoutIntervalMillis)
    assertEquals(sessionReplay.redactAllText(), options.experimental.sessionReplay.redactAllText)
    assertEquals(sessionReplay.redactAllImages(), options.experimental.sessionReplay.redactAllImages)
    assertEquals(sessionReplay.onErrorSampleRate().toDouble(), options.experimental.sessionReplay.onErrorSampleRate)
    assertEquals(sessionReplay.sessionSampleRate().toDouble(), options.experimental.sessionReplay.sessionSampleRate)
    assertEquals(sessionReplay.quality(), options.experimental.sessionReplay.quality.ordinal.toLong())
}

actual fun createSentryPlatformOptionsConfiguration(): PlatformOptionsConfiguration = {
    it.dsn = fakeDsn
}
