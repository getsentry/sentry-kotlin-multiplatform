package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toAndroidSentryOptions(): (SentryAndroidOptions) -> Unit {
    return { sentryAndroidOptions ->
        sentryAndroidOptions.dsn = this.dsn
        sentryAndroidOptions.setBeforeBreadcrumb { breadcrumb, hint ->
            breadcrumb
        }
        sentryAndroidOptions.isAttachThreads = this.attachThreads
        sentryAndroidOptions.isAttachStacktrace = this.attachStackTrace
        this.beforeBreadcrumb?.let {
            sentryAndroidOptions.setBeforeBreadcrumb { androidBreadcrumb, hint ->
                val kmpBreadcrumb = androidBreadcrumb.toSentryBreadcrumb()
                it.invoke(kmpBreadcrumb).toJvmBreadcrumb()
            }
        }
    }
}
