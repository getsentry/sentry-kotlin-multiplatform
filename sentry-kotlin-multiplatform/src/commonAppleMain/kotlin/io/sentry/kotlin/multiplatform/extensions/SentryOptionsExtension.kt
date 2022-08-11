package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryEvent
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import kotlinx.cinterop.convert
import NSException.Sentry.SentryEvent as NSExceptionSentryEvent

internal fun SentryOptions.toCocoaOptionsConfiguration(): (CocoaSentryOptions?) -> Unit = {
    it?.applyBaseOptions(this)
}

/**
 * Applies the given options to this JvmSentryOption
 */
internal fun CocoaSentryOptions.applyBaseOptions(options: SentryOptions) {
    this.dsn = options.dsn
    this.releaseName = options.release
    this.attachStacktrace = options.attachStackTrace
    this.environment = options.environment
    this.debug = options.debug
    this.dist = options.dist
    this.beforeSend = { event ->
        dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as SentryEvent?
    }
}
