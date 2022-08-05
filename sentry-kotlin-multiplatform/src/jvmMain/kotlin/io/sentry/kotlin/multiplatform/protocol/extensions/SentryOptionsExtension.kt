package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions

fun SentryOptions.toJvmSentryOptions(): (JvmSentryOptions) -> Unit {
    return { options ->
        options.dsn = this.dsn
        options.isAttachThreads = this.attachThreads
        options.isAttachStacktrace = this.attachStackTrace
    }
}
