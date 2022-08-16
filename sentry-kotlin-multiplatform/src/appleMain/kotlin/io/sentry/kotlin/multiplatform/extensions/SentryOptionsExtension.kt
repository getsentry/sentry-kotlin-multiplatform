package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryEvent
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.nsexception.dropKotlinCrashEvent
import NSException.Sentry.SentryEvent as NSExceptionSentryEvent

internal fun SentryOptions.toCocoaSentryOptionsCallback(): (CocoaSentryOptions?) -> Unit {
    return { cocoaOptions ->
        cocoaOptions?.dsn = this.dsn
        cocoaOptions?.attachStacktrace = this.attachStackTrace
        cocoaOptions?.beforeSend = { event ->
            dropKotlinCrashEvent(event as NSExceptionSentryEvent?) as SentryEvent?
        }
        this.beforeBreadcrumb?.let { beforeBreadcrumb ->
            cocoaOptions?.setBeforeBreadcrumb { cocoaBreadcrumb ->
                val kmpBreadcrumb = cocoaBreadcrumb?.toKmpBreadcrumb()
                val modifiedKmpBreadcrumb = kmpBreadcrumb?.let(beforeBreadcrumb)
                modifiedKmpBreadcrumb?.toCocoaBreadcrumb() ?: cocoaBreadcrumb
            }
        }
    }
}
