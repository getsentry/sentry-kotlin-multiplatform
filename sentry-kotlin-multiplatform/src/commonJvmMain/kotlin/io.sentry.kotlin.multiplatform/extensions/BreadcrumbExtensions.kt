package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.ISentryBreadcrumb

internal fun ISentryBreadcrumb.toJvmBreadcrumb(): JvmBreadcrumb {
    val outerScope = this
    return JvmBreadcrumb().apply {
        message = outerScope.message
        type = outerScope.type
        category = outerScope.category
        outerScope.getData()?.forEach {
            setData(it.key, it.value)
        }
        level = outerScope.level?.toJvmSentryLevel()
    }
}

internal fun JvmBreadcrumb.toKmpBreadcrumb(): Breadcrumb {
    return Breadcrumb().apply {
        val funScope = this@toKmpBreadcrumb
        message = funScope.message
        type = funScope.type
        category = funScope.category
        setData(funScope.data.toMutableMap())
        level = funScope.level?.toKmpSentryLevel()
    }
}
