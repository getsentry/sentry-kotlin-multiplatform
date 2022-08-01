package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaSentryBreadcrumb
import io.sentry.kotlin.multiplatform.SentryBreadcrumb

fun SentryBreadcrumb.toCocoaBreadcrumb(): CocoaSentryBreadcrumb {
    val cocoaBreadcrumb = CocoaSentryBreadcrumb()
    cocoaBreadcrumb.message = this.getMessage()
    cocoaBreadcrumb.type = this.getType()
    cocoaBreadcrumb.category = this.getCategory().toString()

    val dataClone = HashMap(this.getData() as MutableMap<Any?, Any>)
    cocoaBreadcrumb.setData(dataClone)

    this.getLevel()?.let { cocoaBreadcrumb.setLevel(it.toCocoaSentryLevel()) }
    return cocoaBreadcrumb
}
