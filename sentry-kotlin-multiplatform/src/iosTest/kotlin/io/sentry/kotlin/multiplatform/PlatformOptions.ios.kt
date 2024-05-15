package io.sentry.kotlin.multiplatform

import PrivateSentrySDKOnly.Sentry.PrivateSentrySDKOnly
import io.sentry.kotlin.multiplatform.extensions.toIosOptionsConfiguration
import io.sentry.kotlin.multiplatform.protocol.SdkVersion
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlinx.cinterop.convert
import kotlin.test.assertEquals

actual interface PlatformOptions : CommonPlatformOptions {
    val attachScreenshot: Boolean
    val attachViewHierarchy: Boolean
    val enableAppHangTracking: Boolean
    val appHangTimeoutIntervalMillis: Long
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
}

actual fun createSentryPlatformOptionsConfiguration(): PlatformOptionsConfiguration = {
    it.dsn = fakeDsn
}

actual fun SentryPlatformOptions.toSentryOptions(): SentryOptions {
    val ios = this@toSentryOptions
    return SentryOptions().apply {
        dsn = ios.dsn
        releaseName = ios.releaseName
        sdk = SdkVersion(PrivateSentrySDKOnly.getSdkName()!!, PrivateSentrySDKOnly.getSdkVersionString()!!)
    }
}
