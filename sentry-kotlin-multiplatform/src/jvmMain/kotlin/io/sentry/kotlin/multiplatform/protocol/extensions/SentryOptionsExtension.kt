package io.sentry.kotlin.multiplatform.protocol.extensions

import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.extensions.toJvmBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toKmpBreadcrumb

internal fun SentryOptions.toJvmSentryOptions(): (JvmSentryOptions) -> Unit {
    return { options ->
        options.dsn = this.dsn
        options.isAttachThreads = this.attachThreads
        options.isAttachStacktrace = this.attachStackTrace
        this.beforeBreadcrumb?.let {
            options.setBeforeBreadcrumb { jvmBreadcrumb, hint ->
                val kmpBreadcrumb = jvmBreadcrumb.toKmpBreadcrumb()
                it.invoke(kmpBreadcrumb).toJvmBreadcrumb()
            }
        }
    }
}
