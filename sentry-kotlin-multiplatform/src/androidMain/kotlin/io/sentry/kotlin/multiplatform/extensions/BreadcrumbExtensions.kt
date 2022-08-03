package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.AndroidBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb

fun Breadcrumb.toAndroidBreadcrumb(): AndroidBreadcrumb {
    return breadcrumb
}

fun AndroidBreadcrumb.toKmpBreadcrumb(): Breadcrumb {
    return Breadcrumb(this)
}
