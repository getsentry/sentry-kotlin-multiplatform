package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryEvent
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import kotlinx.cinterop.convert
import NSException.Sentry.SentryEvent as NSExceptionSentryEvent

internal fun SentryOptions.toCocoaSentryOptions() = CocoaSentryOptions().apply {
    val funScope = this@toCocoaSentryOptions
    this.dsn = funScope.dsn
    this.sessionTrackingIntervalMillis = funScope.sessionTrackingIntervalMillis.convert()
    this.enableAutoSessionTracking = funScope.enableAutoSessionTracking
    this.releaseName = funScope.release
    this.attachStacktrace = funScope.attachStackTrace
    this.environment = funScope.environment
    this.debug = funScope.debug
    this.dist = funScope.dist
    this.beforeSend = { event ->
        dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as SentryEvent?
    }
}
