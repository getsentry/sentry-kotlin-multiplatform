package io.sentry.kotlin.multiplatform

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryOptionsCallback
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import io.sentry.SentryReplayOptions as AndroidSentryReplayOptions

actual interface PlatformOptions : CommonPlatformOptions {
    val isAnrEnabled: Boolean
    val anrTimeoutIntervalMillis: Long
    val attachScreenshot: Boolean
    val attachViewHierarchy: Boolean
    val sessionReplay: AndroidSentryReplayOptions
    val proguardUuid: String?
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

    override val diagnosticLevel: SentryLevel
        get() = androidOptions.diagnosticLevel.toKmpSentryLevel()!!

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

    override val sessionReplay: AndroidSentryReplayOptions
        get() = androidOptions.sessionReplay

    override val sendDefaultPii: Boolean
        get() = androidOptions.isSendDefaultPii

    override val proguardUuid: String?
        get() = androidOptions.proguardUuid

    override fun applyFromOptions(options: SentryOptions) {
        options.toAndroidSentryOptionsCallback().invoke(androidOptions)
    }
}

actual fun createPlatformOptions(): PlatformOptions =
    SentryAndroidOptionsWrapper(SentryAndroidOptions())

actual fun PlatformOptions.assertPlatformSpecificOptions(kmpOptions: SentryOptions) {
    val androidOptions = this
    assertEquals(androidOptions.attachScreenshot, kmpOptions.attachScreenshot)
    assertEquals(androidOptions.attachViewHierarchy, kmpOptions.attachViewHierarchy)
    assertEquals(androidOptions.isAnrEnabled, kmpOptions.isAnrEnabled)
    assertEquals(androidOptions.anrTimeoutIntervalMillis, kmpOptions.anrTimeoutIntervalMillis)

    val kmpReplayOptions = kmpOptions.sessionReplay
    assertViewClassMasking(
        kmpReplayOptions.maskAllText,
        androidOptions.sessionReplay.maskViewClasses,
        androidOptions.sessionReplay.unmaskViewClasses,
        AndroidSentryReplayOptions.TEXT_VIEW_CLASS_NAME
    )
    assertViewClassMasking(
        kmpReplayOptions.maskAllImages,
        androidOptions.sessionReplay.maskViewClasses,
        androidOptions.sessionReplay.unmaskViewClasses,
        AndroidSentryReplayOptions.IMAGE_VIEW_CLASS_NAME
    )
    assertEquals(
        androidOptions.sessionReplay.onErrorSampleRate,
        kmpReplayOptions.onErrorSampleRate
    )
    assertEquals(
        androidOptions.sessionReplay.sessionSampleRate,
        kmpReplayOptions.sessionSampleRate
    )
    assertEquals(androidOptions.sessionReplay.quality.name, kmpReplayOptions.quality.name)
    assertEquals(androidOptions.proguardUuid, kmpOptions.proguardUuid)
}

actual fun createSentryPlatformOptionsConfiguration(): PlatformOptionsConfiguration = {
    it.dsn = fakeDsn
}

private fun assertViewClassMasking(
    kmpMaskAll: Boolean,
    maskViewClasses: Collection<String>,
    unmaskViewClasses: Collection<String>,
    viewClassName: String
) {
    if (kmpMaskAll) {
        assertContains(maskViewClasses, viewClassName)
        assertFalse(unmaskViewClasses.contains(viewClassName))
    } else {
        assertFalse(maskViewClasses.contains(viewClassName))
        assertContains(unmaskViewClasses, viewClassName)
    }
}
