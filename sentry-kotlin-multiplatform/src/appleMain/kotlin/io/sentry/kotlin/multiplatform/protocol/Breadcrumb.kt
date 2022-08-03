package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.CocoaBreadcrumb
import io.sentry.kotlin.multiplatform.SentryLevel
import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel

actual class Breadcrumb actual constructor() {

    private var breadcrumb: CocoaBreadcrumb = CocoaBreadcrumb()

    constructor(breadcrumb: CocoaBreadcrumb) : this() {
        this.breadcrumb = breadcrumb
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

    actual fun setType(type: String?) {
        breadcrumb.type = type
    }

    actual fun setCategory(category: String?) {
        category?.let { breadcrumb.category = it }
    }

    actual fun setMessage(message: String?) {
        breadcrumb.message = message
    }

    // Trying to check if breadcrumb.data is null leads to a null pointer exception no matter what
    // Using this boolean flag we can work around it
    private var dataIsNotNull = false

    actual fun setData(key: String, value: Any) {
        if (dataIsNotNull) {
            val previousData = (breadcrumb.data as MutableMap<Any?, Any>).apply {
                put(key, value)
            }
            breadcrumb.setData(previousData)
        } else {
            setData(mutableMapOf(key to value))
        }
    }

    actual fun setData(map: MutableMap<String, Any>) {
        breadcrumb.setData(map as Map<Any?, Any>)
        dataIsNotNull = true
    }

    actual fun setLevel(level: SentryLevel?) {
        level?.let { breadcrumb.level = it.toCocoaSentryLevel() }
    }

    actual fun getData(): MutableMap<String, Any> {
        return breadcrumb.data as MutableMap<String, Any>
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
        return breadcrumb.level.toKmpSentryLevel()
    }
}
