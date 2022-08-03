package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

actual data class BreadcrumbConverter actual constructor(val breadcrumb: Breadcrumb) {

    actual fun getType(): String? {
        return breadcrumb.toCocoaBreadcrumb().type
    }

    actual fun getCategory(): String? {
        return breadcrumb.toCocoaBreadcrumb().category
    }

    actual fun getMessage(): String? {
        return breadcrumb.toCocoaBreadcrumb().message
    }

    actual fun getData(): MutableMap<String, Any> {
        return breadcrumb.toCocoaBreadcrumb().data as MutableMap<String, Any>
    }

    actual fun getLevel(): SentryLevel? {
        return breadcrumb.toCocoaBreadcrumb().level.toKmpSentryLevel()
    }
}
