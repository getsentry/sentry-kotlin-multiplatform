package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.ISentryBreadcrumb

internal fun ISentryBreadcrumb.toCocoaBreadcrumb(): CocoaBreadcrumb {
    val outerScope = this
    return CocoaBreadcrumb().apply {
        outerScope.getMessage().let { setMessage(it) }
        outerScope.getCategory().toString().let { setCategory(it) }

        val dataClone = outerScope.getData().toMap<Any?, Any>()
        setData(dataClone)

        outerScope.getType()?.let { setType(it) }
        outerScope.getLevel()?.let { setLevel(it.toCocoaSentryLevel()) }
    }
}

internal fun CocoaBreadcrumb.toKmpBreadcrumb() = Breadcrumb(this.clone())

internal fun CocoaBreadcrumb.clone(): CocoaBreadcrumb {
    val outerScope = this
    return CocoaBreadcrumb().apply {
        message = outerScope.message
        category = outerScope.category
        level = outerScope.level
        data = outerScope.data
        type = outerScope.type
    }
}
