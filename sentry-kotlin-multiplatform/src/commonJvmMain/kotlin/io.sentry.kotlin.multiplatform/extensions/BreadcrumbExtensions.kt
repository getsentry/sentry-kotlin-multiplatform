package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.ISentryBreadcrumb

internal fun ISentryBreadcrumb.toJvmBreadcrumb(): JvmBreadcrumb {
    val androidBreadcrumb = JvmBreadcrumb()
    androidBreadcrumb.message = this.getMessage()
    androidBreadcrumb.type = this.getType()
    androidBreadcrumb.category = this.getCategory()
    this.getData().forEach {
        androidBreadcrumb.setData(it.key, it.value)
    }
    androidBreadcrumb.level = this.getLevel()?.toJvmSentryLevel()
    return androidBreadcrumb
}

internal fun JvmBreadcrumb.toKmpBreadcrumb(): Breadcrumb {
    return Breadcrumb(this.clone())
}

internal fun JvmBreadcrumb.clone(): JvmBreadcrumb {
    val androidBreadcrumb = JvmBreadcrumb()
    androidBreadcrumb.message = this.message
    androidBreadcrumb.type = this.type
    androidBreadcrumb.category = this.category
    this.data.forEach {
        androidBreadcrumb.setData(it.key, it.value)
    }
    androidBreadcrumb.level = this.level
    return androidBreadcrumb
}
