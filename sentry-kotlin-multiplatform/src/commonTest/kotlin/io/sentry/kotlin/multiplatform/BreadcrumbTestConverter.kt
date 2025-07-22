package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

expect class BreadcrumbTestConverter(breadcrumb: Breadcrumb) {
    fun getType(): String?
    fun getCategory(): String?
    fun getMessage(): String?
    fun getData(): MutableMap<String, Any>
    fun getLevel(): SentryLevel?
}
