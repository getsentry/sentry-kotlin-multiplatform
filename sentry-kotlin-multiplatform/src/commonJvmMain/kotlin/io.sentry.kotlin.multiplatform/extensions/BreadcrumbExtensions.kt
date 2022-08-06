package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.ISentryBreadcrumb

internal fun ISentryBreadcrumb.toJvmBreadcrumb(): JvmBreadcrumb {
    val outerScope = this
    return JvmBreadcrumb().apply {
        message = outerScope.getMessage()
        type = outerScope.getType()
        category = outerScope.getCategory()
        outerScope.getData().forEach {
            setData(it.key, it.value)
        }
        level = outerScope.getLevel()?.toJvmSentryLevel()
    }
}

internal fun JvmBreadcrumb.toKmpBreadcrumb() = Breadcrumb(this.clone())

internal fun JvmBreadcrumb.clone(): JvmBreadcrumb {
    val outerScope = this
    return JvmBreadcrumb().apply {
        message = outerScope.message
        type = outerScope.type
        category = outerScope.category
        outerScope.data.forEach {
            setData(it.key, it.value)
        }
        level = outerScope.level
    }
}
