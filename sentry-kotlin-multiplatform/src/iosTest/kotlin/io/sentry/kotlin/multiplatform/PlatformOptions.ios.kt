package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryReplayOptions
import io.sentry.kotlin.multiplatform.extensions.toIosOptionsConfiguration
import kotlin.test.assertEquals

actual interface ApplePlatformOptions : PlatformOptions {
    val attachScreenshot: Boolean
    val attachViewHierarchy: Boolean
    val enableAppHangTracking: Boolean
    val appHangTimeoutIntervalMillis: Long
    val sessionReplay: SentryReplayOptions
}

class SentryIosOptionsWrapper(private val cocoaOptions: CocoaSentryOptions) : SentryAppleOptionsWrapper(cocoaOptions), ApplePlatformOptions {
    override val attachScreenshot: Boolean
        get() = cocoaOptions.attachScreenshot

    override val attachViewHierarchy: Boolean
        get() = cocoaOptions.attachViewHierarchy

    override val enableAppHangTracking: Boolean
        get() = cocoaOptions.enableAppHangTracking

    override val appHangTimeoutIntervalMillis: Long
        get() = cocoaOptions.appHangTimeoutInterval.toLong()

    override val sessionReplay: SentryReplayOptions
        get() = cocoaOptions.experimental.sessionReplay()

    override fun applyFromOptions(options: SentryOptions) {
        options.toIosOptionsConfiguration().invoke(cocoaOptions)
    }
}

actual fun createApplePlatformOptions(): PlatformOptions = SentryIosOptionsWrapper(CocoaSentryOptions())

actual fun ApplePlatformOptions.assertApplePlatformSpecificOptions(options: SentryOptions) {
    assertEquals(attachScreenshot, options.attachScreenshot)
    assertEquals(attachViewHierarchy, options.attachViewHierarchy)
    assertEquals(enableAppHangTracking, options.enableAppHangTracking)
    assertEquals(appHangTimeoutIntervalMillis, options.appHangTimeoutIntervalMillis)
    assertEquals(sessionReplay.maskAllText(), options.experimental.sessionReplay.maskAllText)
    assertEquals(sessionReplay.maskAllImages(), options.experimental.sessionReplay.maskAllImages)
    assertEquals(sessionReplay.onErrorSampleRate().toDouble(), options.experimental.sessionReplay.onErrorSampleRate)
    assertEquals(sessionReplay.sessionSampleRate().toDouble(), options.experimental.sessionReplay.sessionSampleRate)
    assertEquals(sessionReplay.quality(), options.experimental.sessionReplay.quality.ordinal.toLong())
}
