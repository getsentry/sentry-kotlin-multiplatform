package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.JvmBreadcrumb
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.extensions.toJvmBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toJvmSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpBreadcrumb

actual data class Breadcrumb actual constructor(private val breadcrumb: ISentryBreadcrumb?) : ISentryBreadcrumb {

    private var jvmBreadcrumb = JvmBreadcrumb()
    
    init {
        this.jvmBreadcrumb = breadcrumb?.toJvmBreadcrumb() ?: JvmBreadcrumb()
    }

    constructor(androidBreadcrumb: JvmBreadcrumb) : this() {
        this.jvmBreadcrumb = androidBreadcrumb
    }

    actual companion object {
        actual fun user(category: String, message: String): Breadcrumb {
            return JvmBreadcrumb.user(category, message).toKmpBreadcrumb()
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
        jvmBreadcrumb.type = type
    }

    actual override fun setCategory(category: String?) {
        jvmBreadcrumb.category = category
    }

    actual override fun setMessage(message: String?) {
        jvmBreadcrumb.message = message
    }

    actual override fun setData(key: String, value: Any) {
        jvmBreadcrumb.setData(key, value)
    }

    actual override fun setData(map: MutableMap<String, Any>) {
        for (key in map.keys) {
            map[key]?.let { jvmBreadcrumb.setData(key, it) }
        }
    }

    actual override fun getData(): MutableMap<String, Any> {
        return jvmBreadcrumb.data
    }

    actual override fun setLevel(level: SentryLevel?) {
        jvmBreadcrumb.level = level?.toJvmSentryLevel()
    }

    actual override fun getType(): String? {
        return jvmBreadcrumb.type
    }

    actual override fun getCategory(): String? {
        return jvmBreadcrumb.category
    }

    actual override fun getMessage(): String? {
        return jvmBreadcrumb.message
    }

    actual override fun getLevel(): SentryLevel? {
        return jvmBreadcrumb.level?.toKmpSentryLevel()
    }

    actual override fun clear() {
        this.jvmBreadcrumb = JvmBreadcrumb()
    }

    fun setUnknown(unknown: MutableMap<String, Any>?) {
        jvmBreadcrumb.unknown = unknown
    }

    fun getUnknown(): MutableMap<String, Any>? {
        return jvmBreadcrumb.unknown
    }
}
