package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryEvent
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import NSException.Sentry.SentryEvent as NSExceptionSentryEvent

internal fun SentryOptions.toCocoaSentryOptions(): CocoaSentryOptions {
    val outerScope = this
    return CocoaSentryOptions().apply {
        dsn = outerScope.dsn
        attachStacktrace = outerScope.attachStackTrace
        beforeSend = { event ->
            dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as SentryEvent?
        }
        beforeBreadcrumb = { breadcrumb ->
            val kmpBreadcrumb = breadcrumb?.toSentryBreadcrumb()
            val modifiedBreadcrumb = kmpBreadcrumb?.let { outerScope.beforeBreadcrumb?.invoke(it)?.toCocoaBreadcrumb() }
            modifiedBreadcrumb ?: breadcrumb
        }
    }
}
