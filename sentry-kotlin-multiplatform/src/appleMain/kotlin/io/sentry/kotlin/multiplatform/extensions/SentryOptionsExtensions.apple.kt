package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryHttpStatusCodeRange
import cocoapods.Sentry.experimental
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryEvent
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.log.asSentryLogDelegate
import kotlinx.cinterop.convert
import platform.Foundation.NSNumber

internal fun SentryOptions.toCocoaOptionsConfiguration(): (CocoaSentryOptions?) -> Unit = {
    it?.applyCocoaBaseOptions(this)
}

/**
 * Applies the given options to this CocoaSentryOptions.
 * This avoids code duplication for init on iOS.
 */
internal fun CocoaSentryOptions.applyCocoaBaseOptions(kmpOptions: SentryOptions) {
    val cocoaOptions = this
    cocoaOptions.dsn = kmpOptions.dsn
    cocoaOptions.attachStacktrace = kmpOptions.attachStackTrace
    cocoaOptions.dist = kmpOptions.dist
    kmpOptions.environment?.let {
        cocoaOptions.environment = it
    }
    cocoaOptions.sendDefaultPii = kmpOptions.sendDefaultPii
    cocoaOptions.releaseName = kmpOptions.release
    cocoaOptions.debug = kmpOptions.debug
    cocoaOptions.sessionTrackingIntervalMillis = kmpOptions.sessionTrackingIntervalMillis.convert()
    cocoaOptions.enableAutoSessionTracking = kmpOptions.enableAutoSessionTracking
    cocoaOptions.maxAttachmentSize = kmpOptions.maxAttachmentSize.convert()
    cocoaOptions.maxBreadcrumbs = kmpOptions.maxBreadcrumbs.convert()
    cocoaOptions.enableAppHangTracking = kmpOptions.enableAppHangTracking
    cocoaOptions.enableWatchdogTerminationTracking = kmpOptions.enableWatchdogTerminationTracking
    cocoaOptions.appHangTimeoutInterval = kmpOptions.appHangTimeoutIntervalMillis.toDouble()
    cocoaOptions.diagnosticLevel = kmpOptions.diagnosticLevel.toCocoaSentryLevel()
    cocoaOptions.experimental().setEnableLogs(kmpOptions.logs.enabled)
    kmpOptions.logs.beforeSend?.let { kmpBeforeSend ->
        cocoaOptions.setBeforeSendLog { cocoaLog ->
            cocoaLog?.let {
                val delegate = it.asSentryLogDelegate()
                val result = kmpBeforeSend(delegate)
                if (result != null) cocoaLog else null
            }
        }
    }
    kmpOptions.sampleRate?.let {
        cocoaOptions.sampleRate = NSNumber(double = it)
    }
    kmpOptions.tracesSampleRate?.let {
        cocoaOptions.tracesSampleRate = NSNumber(double = it)
    }
    cocoaOptions.beforeSend = { event ->
        val sdk = event?.sdk?.toMutableMap()

        val packages = kmpOptions.sdk?.packages?.map {
            mapOf("name" to it.name, "version" to it.version)
        }?.toMutableList() ?: mutableListOf()

        sdk?.set("packages", packages)

        event?.sdk = sdk

        if (kmpOptions.beforeSend == null) {
            event
        } else {
            event?.let { SentryEvent(it) }?.let { unwrappedEvent ->
                val result = kmpOptions.beforeSend?.invoke(unwrappedEvent)
                result?.let { event.applyKmpEvent(it) }
            }
        }
    }

    cocoaOptions.beforeBreadcrumb = { cocoaBreadcrumb ->
        if (kmpOptions.beforeBreadcrumb == null) {
            cocoaBreadcrumb
        } else {
            cocoaBreadcrumb?.toKmpBreadcrumb()
                ?.let { kmpOptions.beforeBreadcrumb?.invoke(it) }?.toCocoaBreadcrumb()
        }
    }

    cocoaOptions.enableCaptureFailedRequests = kmpOptions.enableCaptureFailedRequests
    cocoaOptions.failedRequestTargets = kmpOptions.failedRequestTargets
    cocoaOptions.failedRequestStatusCodes = kmpOptions.failedRequestStatusCodes.map {
        SentryHttpStatusCodeRange(
            min = it.min.convert(),
            max = it.max.convert()
        )
    }
}
