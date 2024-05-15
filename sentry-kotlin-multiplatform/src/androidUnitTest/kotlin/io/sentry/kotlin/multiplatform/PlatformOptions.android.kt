package io.sentry.kotlin.multiplatform

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryOptionsCallback
import io.sentry.kotlin.multiplatform.protocol.SdkVersion
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.assertEquals

actual interface PlatformOptions : CommonPlatformOptions {
    val isAnrEnabled: Boolean
    val anrTimeoutIntervalMillis: Long
    val attachScreenshot: Boolean
    val attachViewHierarchy: Boolean
}

class SentryAndroidOptionsWrapper(private val androidOptions: SentryAndroidOptions) :
    PlatformOptions {
    override val dsn: String?
        get() = androidOptions.dsn

    override val attachStackTrace: Boolean
        get() = androidOptions.isAttachStacktrace

    override val release: String?
        get() = androidOptions.release

    override val debug: Boolean
        get() = androidOptions.isDebug

    override val environment: String?
        get() = androidOptions.environment

    override val dist: String?
        get() = androidOptions.dist

    override val enableAutoSessionTracking: Boolean
        get() = androidOptions.isEnableAutoSessionTracking

    override val sessionTrackingIntervalMillis: Long
        get() = androidOptions.sessionTrackingIntervalMillis

    override val maxBreadcrumbs: Int
        get() = androidOptions.maxBreadcrumbs

    override val maxAttachmentSize: Long
        get() = androidOptions.maxAttachmentSize

    override val sampleRate: Double?
        get() = androidOptions.sampleRate

    override val tracesSampleRate: Double?
        get() = androidOptions.tracesSampleRate

    override val isAnrEnabled: Boolean
        get() = androidOptions.isAnrEnabled

    override val anrTimeoutIntervalMillis: Long
        get() = androidOptions.anrTimeoutIntervalMillis

    override val attachScreenshot: Boolean
        get() = androidOptions.isAttachScreenshot

    override val attachViewHierarchy: Boolean
        get() = androidOptions.isAttachViewHierarchy

    override fun applyFromOptions(options: SentryOptions) {
        options.toAndroidSentryOptionsCallback().invoke(androidOptions)
    }
}

actual fun createPlatformOptions(): PlatformOptions =
    SentryAndroidOptionsWrapper(SentryAndroidOptions())

actual fun PlatformOptions.assertPlatformSpecificOptions(options: SentryOptions) {
    assertEquals(attachScreenshot, options.attachScreenshot)
    assertEquals(attachViewHierarchy, options.attachViewHierarchy)
    assertEquals(isAnrEnabled, options.isAnrEnabled)
    assertEquals(anrTimeoutIntervalMillis, options.anrTimeoutIntervalMillis)
}

actual fun createSentryPlatformOptionsConfiguration(): PlatformOptionsConfiguration = {
    it.dsn = fakeDsn
}

actual fun SentryPlatformOptions.toSentryOptions(): SentryOptions {
    val android = this@toSentryOptions
    return SentryOptions().apply {
        dsn = android.dsn
        release = android.release
        sdk = SdkVersion(android.sdkVersion!!.name, android.sdkVersion!!.version)
    }
}
