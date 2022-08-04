package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toAndroidBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

actual data class BreadcrumbTestConverter actual constructor(val breadcrumb: Breadcrumb) {

    actual fun getType(): String? {
        return breadcrumb.toAndroidBreadcrumb().type
    }

    actual fun getCategory(): String? {
        return breadcrumb.toAndroidBreadcrumb().category
    }

    actual fun getMessage(): String? {
        return breadcrumb.toAndroidBreadcrumb().message
    }

    actual fun getData(): MutableMap<String, Any> {
        return breadcrumb.toAndroidBreadcrumb().data
    }

    actual fun getLevel(): SentryLevel? {
        return breadcrumb.toAndroidBreadcrumb().level?.toKmpSentryLevel()
    }
}
