package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.AndroidBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.ISentryBreadcrumb

fun ISentryBreadcrumb.toAndroidBreadcrumb(): AndroidBreadcrumb {
    val androidBreadcrumb = AndroidBreadcrumb()
    androidBreadcrumb.message = this.getMessage()
    androidBreadcrumb.type = this.getType()
    androidBreadcrumb.category = this.getCategory()
    this.getData().forEach {
        androidBreadcrumb.setData(it.key, it.value)
    }
    androidBreadcrumb.level = this.getLevel()?.toAndroidSentryLevel()
    return androidBreadcrumb
}

fun AndroidBreadcrumb.toKmpBreadcrumb(): Breadcrumb {
    return Breadcrumb(this.clone())
}

fun AndroidBreadcrumb.clone(): AndroidBreadcrumb {
    val androidBreadcrumb = AndroidBreadcrumb()
    androidBreadcrumb.message = this.message
    androidBreadcrumb.type = this.type
    androidBreadcrumb.category = this.category
    this.data.forEach {
        androidBreadcrumb.setData(it.key, it.value)
    }
    androidBreadcrumb.level = this.level
    return androidBreadcrumb
}
