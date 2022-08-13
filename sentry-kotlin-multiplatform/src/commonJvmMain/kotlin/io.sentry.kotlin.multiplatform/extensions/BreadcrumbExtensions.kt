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
