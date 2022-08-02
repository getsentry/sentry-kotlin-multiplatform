package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaSentryBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryBreadcrumb

fun SentryBreadcrumb.toCocoaBreadcrumb(): CocoaSentryBreadcrumb {
    val cocoaBreadcrumb = CocoaSentryBreadcrumb()
    this.getMessage().let { cocoaBreadcrumb.setMessage(it) }
    this.getCategory().toString().let { cocoaBreadcrumb.setCategory(it) }

    this.getData().let {
        if (it != null) {
            val dataClone = HashMap(it as MutableMap<Any?, Any>)
            cocoaBreadcrumb.setData(dataClone)
        }
    }

    this.getType()?.let { cocoaBreadcrumb.setType(it) }
    this.getLevel()?.let { cocoaBreadcrumb.setLevel(it.toCocoaSentryLevel()) }
    return cocoaBreadcrumb
}
