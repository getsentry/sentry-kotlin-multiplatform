package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.CocoaBreadcrumb
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.extensions.toCocoaBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel

actual data class Breadcrumb actual constructor(val breadcrumb: ISentryBreadcrumb?) : ISentryBreadcrumb {

    private var cocoaBreadcrumb: CocoaBreadcrumb = CocoaBreadcrumb()

    init {
        this.cocoaBreadcrumb = breadcrumb?.toCocoaBreadcrumb() ?: CocoaBreadcrumb()
    }

    constructor(cocoaBreadcrumb: CocoaBreadcrumb) : this() {
        this.cocoaBreadcrumb = cocoaBreadcrumb
    }

    actual companion object {
        actual fun user(category: String, message: String): Breadcrumb {
            return BreadcrumbFactory.user(category, message)
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

        actual fun userInteraction(subCategory: String, viewId: String?, viewClass: String?, additionalData: Map<String?, Any?>): Breadcrumb {
            return BreadcrumbFactory.userInteraction(
                subCategory,
                viewId,
                viewClass,
                additionalData
            )
        }
    }

    actual override fun setType(type: String?) {
        cocoaBreadcrumb.type = type
    }

    actual override fun setCategory(category: String?) {
        category?.let { cocoaBreadcrumb.category = it }
    }

    actual override fun setMessage(message: String?) {
        cocoaBreadcrumb.message = message
    }

    // Trying to check if breadcrumb.data is null leads to a null pointer exception no matter what
    // Using this boolean flag we can work around it
    private var dataIsNotNull = false

    actual override fun setData(key: String, value: Any) {
        if (dataIsNotNull) {
            val previousData = (cocoaBreadcrumb.data as MutableMap<Any?, Any>).apply {
                put(key, value)
            }
            cocoaBreadcrumb.setData(previousData)
        } else {
            setData(mutableMapOf(key to value))
        }
    }

    actual override fun setData(map: MutableMap<String, Any>) {
        cocoaBreadcrumb.setData(map as Map<Any?, Any>)
        dataIsNotNull = true
    }

    actual override fun setLevel(level: SentryLevel?) {
        level?.let { cocoaBreadcrumb.level = it.toCocoaSentryLevel() }
    }

    actual override fun getData(): MutableMap<String, Any> {
        return cocoaBreadcrumb.data as MutableMap<String, Any>
    }

    actual override fun getType(): String? {
        return cocoaBreadcrumb.type
    }

    actual override fun getCategory(): String? {
        return cocoaBreadcrumb.category
    }

    actual override fun getMessage(): String? {
        return cocoaBreadcrumb.message
    }

    actual override fun getLevel(): SentryLevel? {
        return cocoaBreadcrumb.level.toKmpSentryLevel()
    }
}
