package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.ISentryBreadcrumb

internal fun ISentryBreadcrumb.toCocoaBreadcrumb(): CocoaBreadcrumb {
    val outerScope = this
    return CocoaBreadcrumb().apply {
        setMessage(outerScope.message)
        setType(outerScope.type)
        outerScope.category?.let { setCategory(it) }
        outerScope.level?.let { setLevel(it.toCocoaSentryLevel()) }
        setData(outerScope.getData()?.toMap())
    }
}
