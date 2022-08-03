package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.AndroidBreadcrumb
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpBreadcrumb

actual class Breadcrumb actual constructor() {

    private var breadcrumb = AndroidBreadcrumb()

    constructor(breadcrumb: AndroidBreadcrumb) : this() {
        this.breadcrumb = breadcrumb
    }

    actual companion object {
        actual fun user(category: String, message: String): Breadcrumb {
            return AndroidBreadcrumb.user(category, message).toKmpBreadcrumb()
        }

        actual fun http(url: String, method: String): Breadcrumb {
            return BreadcrumbFactory.http(url, method)
        }

        actual fun http(url: String, method: String, code: Int?): Breadcrumb {
            return BreadcrumbFactory.http(url, method, code)
        }

        actual fun navigation(from: String, to: String): Breadcrumb {
            return BreadcrumbFactory.navigation(from, to)
        }

        actual fun transaction(message: String): Breadcrumb {
            return BreadcrumbFactory.transaction(message)
        }

        actual fun debug(message: String): Breadcrumb {
            return BreadcrumbFactory.debug(message)
        }

        actual fun error(message: String): Breadcrumb {
            return BreadcrumbFactory.error(message)
        }

        actual fun info(message: String): Breadcrumb {
            return BreadcrumbFactory.info(message)
        }

        actual fun query(message: String): Breadcrumb {
            return BreadcrumbFactory.query(message)
        }

        actual fun ui(category: String, message: String): Breadcrumb {
            return BreadcrumbFactory.ui(category, message)
        }

        actual fun userInteraction(subCategory: String, viewId: String?, viewClass: String?): Breadcrumb {
            return BreadcrumbFactory.userInteraction(subCategory, viewId, viewClass)
        }

        actual fun userInteraction(
            subCategory: String,
            viewId: String?,
            viewClass: String?,
            additionalData: Map<String?, Any?>
        ): Breadcrumb {
            return BreadcrumbFactory.userInteraction(
                subCategory,
                viewId,
                viewClass,
                additionalData
            )
        }
    }

    actual fun setType(type: String?) {
        breadcrumb.type = type
    }

    actual fun setCategory(category: String?) {
        breadcrumb.category = category
    }

    actual fun setMessage(message: String?) {
        breadcrumb.message = message
    }

    actual fun setData(key: String, value: Any) {
        breadcrumb.setData(key, value)
    }

    actual fun setData(map: MutableMap<String, Any>) {
        for (key in map.keys) {
            map[key]?.let { breadcrumb.setData(key, it) }
        }
    }

    actual fun getData(): MutableMap<String, Any> {
        return breadcrumb.data
    }

    actual fun setLevel(level: SentryLevel?) {
        breadcrumb.level = level?.toAndroidSentryLevel()
    }

    actual fun getType(): String? {
        return breadcrumb.type
    }

    actual fun getCategory(): String? {
        return breadcrumb.category
    }

    actual fun getMessage(): String? {
        return breadcrumb.message
    }

    actual fun getLevel(): SentryLevel? {
        return breadcrumb.level?.toKmpSentryLevel()
    }

    fun setUnknown(unknown: MutableMap<String, Any>?) {
        breadcrumb.unknown = unknown
    }

    fun getUnknown(): MutableMap<String, Any>? {
        return breadcrumb.unknown
    }
}
