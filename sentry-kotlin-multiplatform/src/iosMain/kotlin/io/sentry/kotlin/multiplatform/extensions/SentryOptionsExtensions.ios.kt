package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryReplayOptions
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toIosOptionsConfiguration(): (CocoaSentryOptions?) -> Unit = { options ->
    options?.let { cocoaOptions ->
        val kmpOptions = this@toIosOptionsConfiguration

        // Apply base options available to all Cocoa/Apple targets
        cocoaOptions.applyCocoaBaseOptions(kmpOptions)

        // Apply iOS specific options
        cocoaOptions.attachScreenshot = this.attachScreenshot
        cocoaOptions.attachViewHierarchy = this.attachViewHierarchy

        // Replay options
        val replayOptions = SentryReplayOptions(
            dictionary = mapOf(
                // Setting the onErrorSampleRate like this, using setOnErrorSampleRate
                // crashes on compose multiplatform for some unknown reason
                "errorSampleRate" to kmpOptions.experimental.sessionReplay.onErrorSampleRate?.toFloat()
            )
        ).apply {
            setMaskAllText(kmpOptions.experimental.sessionReplay.maskAllText)
            setMaskAllImages(kmpOptions.experimental.sessionReplay.maskAllImages)
            kmpOptions.experimental.sessionReplay.sessionSampleRate?.let { setSessionSampleRate(it.toFloat()) }
            setQuality(kmpOptions.experimental.sessionReplay.quality.ordinal.toLong())
        }
        cocoaOptions.setSessionReplay(replayOptions)
    } ?: run {
        // Log a warning if options is null
        // TODO: Replace with actual logging when a logger is available
        println("Warning: CocoaSentryOptions is null, skipping iOS configuration")
    }
}
