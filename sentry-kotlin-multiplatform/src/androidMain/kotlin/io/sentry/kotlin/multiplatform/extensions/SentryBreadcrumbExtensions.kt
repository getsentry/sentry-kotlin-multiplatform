package io.sentry.kotlin.multiplatform.extensions

import android.util.Log
import io.sentry.kotlin.multiplatform.AndroidSentryBreadcrumb
import io.sentry.kotlin.multiplatform.SentryBreadcrumb
import io.sentry.util.CollectionUtils

fun SentryBreadcrumb.toAndroidBreadcrumb(): AndroidSentryBreadcrumb {
    val androidBreadcrumb = AndroidSentryBreadcrumb()
    androidBreadcrumb.message = this.getMessage()
    androidBreadcrumb.type = this.getType()
    androidBreadcrumb.category = this.getCategory()
    val dataClone = CollectionUtils.newConcurrentHashMap(this.getData() as MutableMap<String?, Any>)
    if (dataClone != null) {
        for ((key, value) in dataClone) {
            androidBreadcrumb.setData(key!!, value)
        }
    }
    if (this.getUnknown() != null) {
        androidBreadcrumb.unknown = CollectionUtils.newConcurrentHashMap(this.getUnknown() as MutableMap<String, Any>)
    }
    androidBreadcrumb.level = this.getLevel()?.toAndroidSentryLevel()
    return androidBreadcrumb
}