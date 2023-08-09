package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

internal fun Breadcrumb.toJvmBreadcrumb() = JvmBreadcrumb().apply {
    val scope = this@toJvmBreadcrumb
    message = scope.message
    type = scope.type
    category = scope.category
    scope.getData()?.forEach {
        setData(it.key, it.value)
    }
    level = scope.level?.toJvmSentryLevel()
}

internal fun JvmBreadcrumb.toKmpBreadcrumb() = Breadcrumb().apply {
    val scope = this@toKmpBreadcrumb
    message = scope.message
    type = scope.type
    category = scope.category
    setData(scope.data.toMutableMap())
    level = scope.level?.toKmpSentryLevel()
}
