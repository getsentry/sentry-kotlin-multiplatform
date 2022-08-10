package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryEvent
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import kotlinx.cinterop.convert
import NSException.Sentry.SentryEvent as NSExceptionSentryEvent

internal fun SentryOptions.toCocoaSentryOptionsCallback(): (CocoaSentryOptions?) -> Unit = {
    it?.dsn = this.dsn
    it?.releaseName = this.release
    it?.attachStacktrace = this.attachStackTrace
    it?.environment = this.environment
    it?.debug = this.debug
    it?.dist = this.dist
    it?.beforeSend = { event ->
        dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as SentryEvent?
    }
}
