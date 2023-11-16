package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

internal fun Breadcrumb.toCocoaBreadcrumb() = CocoaBreadcrumb().apply {
    val scope = this@toCocoaBreadcrumb
    setMessage(scope.message)
    setType(scope.type)
    scope.category?.let { setCategory(it) }
    scope.level?.let { setLevel(it.toCocoaSentryLevel()) }
    setData(scope.getData()?.toMap())
}

internal fun CocoaBreadcrumb.toKmpBreadcrumb() = Breadcrumb().apply {
    val scope = this@toKmpBreadcrumb
    message = scope.message
    type = scope.type
    category = scope.category
    val map = scope.data as? Map<String, Any>
    map?.let {
        this.setData(it.toMutableMap())
    }
    level = scope.level?.toKmpSentryLevel()
}
