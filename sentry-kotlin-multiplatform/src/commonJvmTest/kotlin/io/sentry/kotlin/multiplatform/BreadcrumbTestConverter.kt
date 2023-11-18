package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toJvmBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

actual data class BreadcrumbTestConverter actual constructor(val breadcrumb: Breadcrumb) {

  actual fun getType(): String? {
    return breadcrumb.toJvmBreadcrumb().type
  }

  actual fun getCategory(): String? {
    return breadcrumb.toJvmBreadcrumb().category
  }

  actual fun getMessage(): String? {
    return breadcrumb.toJvmBreadcrumb().message
  }

  actual fun getData(): MutableMap<String, Any> {
    return breadcrumb.toJvmBreadcrumb().data
  }

  actual fun getLevel(): SentryLevel? {
    return breadcrumb.toJvmBreadcrumb().level?.toKmpSentryLevel()
  }
}
