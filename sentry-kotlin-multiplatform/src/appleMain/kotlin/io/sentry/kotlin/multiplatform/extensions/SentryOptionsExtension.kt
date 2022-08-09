package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryEvent
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import cocoapods.Sentry.SentryOptions as S
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import kotlinx.cinterop.convert
import NSException.Sentry.SentryEvent as NSExceptionSentryEvent

internal fun SentryOptions.toCocoaSentryOptions() = CocoaSentryOptions().apply {
    this.dsn = this@toCocoaSentryOptions.dsn
    this.sessionTrackingIntervalMillis = this@toCocoaSentryOptions.sessionTrackingIntervalMillis.convert()
    this.enableAutoSessionTracking = this@toCocoaSentryOptions.enableAutoSessionTracking
    this.releaseName = this@toCocoaSentryOptions.release
    this.attachStacktrace = this@toCocoaSentryOptions.attachStackTrace
    this.environment = this@toCocoaSentryOptions.environment
    this.debug = this@toCocoaSentryOptions.debug
    this.dist = this@toCocoaSentryOptions.dist
    this.enableAppHangTracking = this@toCocoaSentryOptions.anrEnabled
    this.appHangTimeoutInterval = this@toCocoaSentryOptions.anrTimeoutIntervalMillis.convert()
    this.beforeSend = { event ->
        dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as SentryEvent?
    }
}
