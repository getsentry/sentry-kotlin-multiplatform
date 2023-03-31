package io.sentry.kotlin.multiplatform.extensions

import PrivateSentrySDKOnly.Sentry.PrivateSentrySDKOnly
import cocoapods.Sentry.SentryEvent
import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import kotlinx.cinterop.convert
import NSException.Sentry.SentryEvent as NSExceptionSentryEvent

internal fun SentryOptions.toCocoaOptionsConfiguration(): (CocoaSentryOptions?) -> Unit = {
    it?.applyCocoaBaseOptions(this)
}

/**
 * Applies the given options to this CocoaSentryOptions.
 * This avoids code duplication for init on iOS.
 */
internal fun CocoaSentryOptions.applyCocoaBaseOptions(options: SentryOptions) {
    this.dsn = options.dsn
    this.attachStacktrace = options.attachStackTrace
    this.dist = options.dist
    options.environment?.let {
        this.environment = it
    }
    this.releaseName = options.release
    this.debug = options.debug
    this.sessionTrackingIntervalMillis = options.sessionTrackingIntervalMillis.convert()
    this.enableAutoSessionTracking = options.enableAutoSessionTracking
    this.maxAttachmentSize = options.maxAttachmentSize.convert()
    this.maxBreadcrumbs = options.maxBreadcrumbs.convert()
    this.beforeSend = { event ->
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
        dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as SentryEvent?
    }

    val sdkName = options.sdk?.name ?: BuildKonfig.SENTRY_KMP_COCOA_SDK_NAME
    val sdkVersion = options.sdk?.version ?: BuildKonfig.VERSION_NAME
    PrivateSentrySDKOnly.setSdkName(sdkName, sdkVersion)

    this.beforeBreadcrumb = { cocoaBreadcrumb ->
        cocoaBreadcrumb?.toKmpBreadcrumb()
            ?.let { options.beforeBreadcrumb?.invoke(it) }?.toCocoaBreadcrumb()
    }
}
