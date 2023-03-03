package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryEvent
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import io.sentry.kotlin.multiplatform.protocol.SdkVersion
import io.sentry.kotlin.multiplatform.protocol.addPackage
import io.sentry.sentry_kotlin_multiplatform.BuildConfig
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
            BuildConfig.SENTRY_KOTLIN_MULTIPLATFORM_SDK_NAME,
            BuildConfig.VERSION_NAME
        ).apply {
            addPackage("cocoapods:sentry-cocoa", "8.2.0")
        }.toCocoaSdkVersion()
        event?.setSdk(sdk)
        event
    }
}
