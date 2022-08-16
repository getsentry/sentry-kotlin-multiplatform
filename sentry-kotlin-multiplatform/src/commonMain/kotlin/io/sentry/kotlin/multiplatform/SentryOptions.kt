package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

open class SentryOptions {
    var dsn: String? = null
    var attachStackTrace = true
    var attachThreads = true
    var beforeBreadcrumb: ((Breadcrumb) -> Breadcrumb)? = null
}
