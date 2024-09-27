package io.sentry.kotlin.multiplatform.extensions

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
        cocoaOptions.experimental.sessionReplay().apply {
            setRedactAllText(kmpOptions.experimental.sessionReplay.redactAllText)
            setRedactAllImages(kmpOptions.experimental.sessionReplay.redactAllImages)
            kmpOptions.experimental.sessionReplay.sessionSampleRate?.let { setSessionSampleRate(it.toFloat()) }
            kmpOptions.experimental.sessionReplay.onErrorSampleRate?.let { setOnErrorSampleRate(it.toFloat()) }
        }
    } ?: run {
        // Log a warning if options is null
        // TODO: Replace with actual logging when a logger is available
        println("Warning: CocoaSentryOptions is null, skipping iOS configuration")
    }
}
