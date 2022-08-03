package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

fun Breadcrumb.toCocoaBreadcrumb(): CocoaBreadcrumb {
    val cocoaBreadcrumb = CocoaBreadcrumb()
    this.getMessage().let { cocoaBreadcrumb.setMessage(it) }
    this.getCategory().toString().let { cocoaBreadcrumb.setCategory(it) }

    this.getData().let {
        val dataClone = HashMap(it as MutableMap<Any?, Any>)
        cocoaBreadcrumb.setData(dataClone)
    }

    this.getType()?.let { cocoaBreadcrumb.setType(it) }
    this.getLevel()?.let { cocoaBreadcrumb.setLevel(it.toCocoaSentryLevel()) }
    return cocoaBreadcrumb
}

fun CocoaBreadcrumb.toKmpBreadcrumb(): Breadcrumb {
    return Breadcrumb(this.clone())
}

fun CocoaBreadcrumb.clone(): CocoaBreadcrumb {
    val cocoaBreadcrumb = CocoaBreadcrumb()
    cocoaBreadcrumb.message = this.message
    cocoaBreadcrumb.category = this.category
    cocoaBreadcrumb.level = this.level
    cocoaBreadcrumb.data = this.data
    cocoaBreadcrumb.type = this.type
    return cocoaBreadcrumb
}
