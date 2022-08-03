package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.AndroidBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.util.CollectionUtils

fun Breadcrumb.toAndroidBreadcrumb(): AndroidBreadcrumb {
    val androidBreadcrumb = AndroidBreadcrumb()
    androidBreadcrumb.message = this.getMessage()
    androidBreadcrumb.type = this.getType()
    androidBreadcrumb.category = this.getCategory()
    val dataClone = CollectionUtils.newConcurrentHashMap(this.getData() as MutableMap<String?, Any>)
    if (dataClone != null) {
        for ((key, value) in dataClone) {
            androidBreadcrumb.setData(key!!, value)
        }
    }
    androidBreadcrumb.level = this.getLevel()?.toAndroidSentryLevel()
    return androidBreadcrumb
}

fun AndroidBreadcrumb.toKmpBreadcrumb(): Breadcrumb {
    return Breadcrumb(this)
}
