package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

actual data class BreadcrumbTestConverter actual constructor(
    val breadcrumb: Breadcrumb,
) {
    actual fun getType(): String? = breadcrumb.toCocoaBreadcrumb().type

    actual fun getCategory(): String? = breadcrumb.toCocoaBreadcrumb().category

    actual fun getMessage(): String? = breadcrumb.toCocoaBreadcrumb().message

    actual fun getData(): MutableMap<String, Any> = breadcrumb.toCocoaBreadcrumb().data as MutableMap<String, Any>

    actual fun getLevel(): SentryLevel? = breadcrumb.toCocoaBreadcrumb().level.toKmpSentryLevel()
}
