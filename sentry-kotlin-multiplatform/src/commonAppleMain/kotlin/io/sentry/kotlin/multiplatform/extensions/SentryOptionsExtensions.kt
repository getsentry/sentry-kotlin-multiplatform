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
    this.beforeSend = { event ->
        dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as SentryEvent?

        val cocoaName = BuildKonfig.SENTRY_COCOA_PACKAGE_NAME
        val cocoaVersion = BuildKonfig.SENTRY_COCOA_VERSION
        options.sdk.apply {
            val names = this.packages?.map { it.name }
            if (names?.contains(cocoaName) != true) {
                this.addPackage(cocoaName, cocoaVersion)
            }
        }
        val sdk = event?.sdk?.toMutableMap()
        sdk?.set(
            "packages",
            options.sdk.packages?.map {
                mapOf("name" to it.name, "version" to it.version)
            }
        )
        event?.sdk = sdk

        event
    }
    PrivateSentrySDKOnly.setSdkName(options.sdk.name, options.sdk.version)
    this.beforeBreadcrumb = { cocoaBreadcrumb ->
        cocoaBreadcrumb?.toKmpBreadcrumb()
            .apply { this?.let { options.beforeBreadcrumb?.invoke(it) } }?.toCocoaBreadcrumb()
    }
}
