package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryEvent
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
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
    this.releaseName = options.release
    this.attachStacktrace = options.attachStackTrace
    this.environment = options.environment
    this.debug = options.debug
    this.dist = options.dist
    this.beforeSend = { event ->
        dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as SentryEvent?
    }
}
