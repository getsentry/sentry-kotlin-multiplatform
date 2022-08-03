package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.AndroidBreadcrumb
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.extensions.toAndroidBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpBreadcrumb

actual data class Breadcrumb actual constructor(private val breadcrumb: ISentryBreadcrumb?) : ISentryBreadcrumb {

    private var androidBreadcrumb = AndroidBreadcrumb()
    
    init {
        this.androidBreadcrumb = breadcrumb?.toAndroidBreadcrumb() ?: AndroidBreadcrumb()
    }

    constructor(androidBreadcrumb: AndroidBreadcrumb) : this() {
        this.androidBreadcrumb = androidBreadcrumb
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

    actual override fun setType(type: String?) {
        androidBreadcrumb.type = type
    }

    actual override fun setCategory(category: String?) {
        androidBreadcrumb.category = category
    }

    actual override fun setMessage(message: String?) {
        androidBreadcrumb.message = message
    }

    actual override fun setData(key: String, value: Any) {
        androidBreadcrumb.setData(key, value)
    }

    actual override fun setData(map: MutableMap<String, Any>) {
        for (key in map.keys) {
            map[key]?.let { androidBreadcrumb.setData(key, it) }
        }
    }

    actual override fun getData(): MutableMap<String, Any> {
        return androidBreadcrumb.data
    }

    actual override fun setLevel(level: SentryLevel?) {
        androidBreadcrumb.level = level?.toAndroidSentryLevel()
    }

    actual override fun getType(): String? {
        return androidBreadcrumb.type
    }

    actual override fun getCategory(): String? {
        return androidBreadcrumb.category
    }

    actual override fun getMessage(): String? {
        return androidBreadcrumb.message
    }

    actual override fun getLevel(): SentryLevel? {
        return androidBreadcrumb.level?.toKmpSentryLevel()
    }

    actual override fun clear() {
        this.androidBreadcrumb = AndroidBreadcrumb()
    }

    fun setUnknown(unknown: MutableMap<String, Any>?) {
        androidBreadcrumb.unknown = unknown
    }

    fun getUnknown(): MutableMap<String, Any>? {
        return androidBreadcrumb.unknown
    }
}
