package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toAndroidSentryOptions(): (SentryAndroidOptions) -> Unit {
    return { sentryAndroidOptions ->
        sentryAndroidOptions.dsn = this.dsn
        sentryAndroidOptions.isAttachThreads = this.attachThreads
        sentryAndroidOptions.isAttachStacktrace = this.attachStackTrace
    }
}
