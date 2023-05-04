package io.sentry.kotlin.multiplatform.extensions

import PrivateSentrySDKOnly.Sentry.PrivateSentrySDKOnly
import cocoapods.Sentry.SentryHttpStatusCodeRange
import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.CocoaSentryEvent
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.CocoaTransactionContextProvider
import io.sentry.kotlin.multiplatform.SamplingContext
import io.sentry.kotlin.multiplatform.SentryEvent
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.TransactionContextAdapter
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
    tracesSampleRate = options.tracesSampleRate?.let { NSNumber(it) }
    options.environment?.let {
        environment = it
    }
    releaseName = options.release
    debug = options.debug
    sessionTrackingIntervalMillis = options.sessionTrackingIntervalMillis.convert()
    enableAutoSessionTracking = options.enableAutoSessionTracking
    maxAttachmentSize = options.maxAttachmentSize.convert()
    maxBreadcrumbs = options.maxBreadcrumbs.convert()
    beforeSend = { event ->
        val cocoaName = BuildKonfig.SENTRY_COCOA_PACKAGE_NAME
        val cocoaVersion = BuildKonfig.SENTRY_COCOA_VERSION

        val sdk = event?.sdk?.toMutableMap()

        val packages = options.sdk?.packages?.map {
            mapOf("name" to it.name, "version" to it.version)
        }?.toMutableList() ?: mutableListOf()

        val names = packages.map { it["name"] }
        if (!names.contains(cocoaName)) {
            packages.add(mapOf("name" to cocoaName, "version" to cocoaVersion))
        }

        sdk?.set("packages", packages)

        event?.sdk = sdk

        if (options.beforeSend == null) {
            dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as CocoaSentryEvent?
        } else {
            val modifiedEvent = event?.let { SentryEvent(it) }?.let { unwrappedEvent ->
                val result = options.beforeSend?.invoke(unwrappedEvent)
                result?.let { event.applyKmpEvent(it) }
            }
            dropKotlinCrashEvent(modifiedEvent as NSExceptionSentryEvent?) as CocoaSentryEvent?
        }
    }

    val sdkName = options.sdk?.name ?: BuildKonfig.SENTRY_KMP_COCOA_SDK_NAME
    val sdkVersion = options.sdk?.version ?: BuildKonfig.VERSION_NAME
    PrivateSentrySDKOnly.setSdkName(sdkName, sdkVersion)

    beforeBreadcrumb = { cocoaBreadcrumb ->
        cocoaBreadcrumb?.toKmpBreadcrumb()?.let { options.beforeBreadcrumb?.invoke(it) }
            ?.toCocoaBreadcrumb()
    }

    tracesSampler = {
        it?.let { context ->
            val cocoaTransactionContext = CocoaTransactionContextProvider(context.transactionContext)
            val transactionContext = TransactionContextAdapter(cocoaTransactionContext)
            val samplingContext = SamplingContext(transactionContext)
            // returns null if KMP tracesSampler is null
            val sampleRate = options.tracesSampler?.invoke(samplingContext)
            sampleRate?.let { unwrappedSampleRate ->
                NSNumber(unwrappedSampleRate)
            }
        }
    }

    enableCaptureFailedRequests = options.enableCaptureFailedRequests
    failedRequestTargets = options.failedRequestTargets
    failedRequestStatusCodes = options.failedRequestStatusCodes.map {
        SentryHttpStatusCodeRange(
            min = it.min.convert(), max = it.max.convert()
        )
    }
}
