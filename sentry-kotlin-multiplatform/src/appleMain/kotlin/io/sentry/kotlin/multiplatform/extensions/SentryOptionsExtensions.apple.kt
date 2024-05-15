package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryHttpStatusCodeRange
import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.CocoaSentryEvent
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryEvent
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.SentryPlatformOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import kotlinx.cinterop.convert
import platform.Foundation.NSNumber
import NSException.Sentry.SentryEvent as NSExceptionSentryEvent

internal fun SentryOptions.toCocoaOptionsConfiguration(): (CocoaSentryOptions?) -> Unit = {
    it?.applyCocoaBaseOptions(this)
}

/**
 * Applies the given options to this CocoaSentryOptions.
 * This avoids code duplication for init on iOS.
 */
internal fun CocoaSentryOptions.applyCocoaBaseOptions(options: SentryOptions) {
    dsn = options.dsn
    attachStacktrace = options.attachStackTrace
    dist = options.dist
    options.environment?.let {
        environment = it
    }
    releaseName = options.release
    debug = options.debug
    sessionTrackingIntervalMillis = options.sessionTrackingIntervalMillis.convert()
    enableAutoSessionTracking = options.enableAutoSessionTracking
    maxAttachmentSize = options.maxAttachmentSize.convert()
    maxBreadcrumbs = options.maxBreadcrumbs.convert()
    enableAppHangTracking = options.enableAppHangTracking
    appHangTimeoutInterval = options.appHangTimeoutIntervalMillis.toDouble()
    options.sampleRate?.let {
        sampleRate = NSNumber(double = it)
    }
    options.tracesSampleRate?.let {
        tracesSampleRate = NSNumber(double = it)
    }
    beforeSend = { event ->
        val sdk = event?.sdk?.toMutableMap()

        val packages = options.sdk?.packages?.map {
            mapOf("name" to it.name, "version" to it.version)
        }?.toMutableList() ?: mutableListOf()

        sdk?.set("packages", packages)

        event?.sdk = sdk

        event?.let { SentryEvent(it) }?.let { unwrappedEvent ->
            val result = options.beforeSend?.invoke(unwrappedEvent)
            result?.let { event.applyKmpEvent(it) }
        }
    }

    beforeBreadcrumb = { cocoaBreadcrumb ->
        if (options.beforeBreadcrumb == null) {
            cocoaBreadcrumb
        } else {
            cocoaBreadcrumb?.toKmpBreadcrumb()
                ?.let { options.beforeBreadcrumb?.invoke(it) }?.toCocoaBreadcrumb()
        }
    }

    enableCaptureFailedRequests = options.enableCaptureFailedRequests
    failedRequestTargets = options.failedRequestTargets
    failedRequestStatusCodes = options.failedRequestStatusCodes.map {
        SentryHttpStatusCodeRange(
            min = it.min.convert(),
            max = it.max.convert()
        )
    }
}
