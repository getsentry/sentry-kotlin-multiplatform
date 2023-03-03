package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryEvent
import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import io.sentry.kotlin.multiplatform.protocol.SdkVersion
import io.sentry.kotlin.multiplatform.protocol.addPackage
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
        val sdk = SdkVersion(
            BuildKonfig.SENTRY_KOTLIN_MULTIPLATFORM_SDK_NAME,
            BuildKonfig.VERSION_NAME
        ).apply {
            addPackage("cocoapods:sentry-cocoa", BuildKonfig.SENTRY_COCOA_VERSION)
        }.toCocoaSdkVersion()
        event?.setSdk(sdk)
        event
    }
    this.beforeBreadcrumb = { cocoaBreadcrumb ->
        cocoaBreadcrumb
            ?.toKmpBreadcrumb()
            .apply { this?.let { options.beforeBreadcrumb?.invoke(it) } }
            ?.toCocoaBreadcrumb()
    }
}
