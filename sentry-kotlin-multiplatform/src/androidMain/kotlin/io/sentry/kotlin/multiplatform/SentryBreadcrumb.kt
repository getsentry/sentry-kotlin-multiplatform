package io.sentry.kotlin.multiplatform

import io.sentry.Breadcrumb
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryLevel

actual class SentryBreadcrumb {

    private var breadcrumb: Breadcrumb = Breadcrumb()

    actual companion object {
        actual fun user(category: String, message: String): SentryBreadcrumb {
            val breadcrumb = SentryBreadcrumb()
            breadcrumb.setCategory(category)
            breadcrumb.setMessage(message)
            return breadcrumb
        }

        actual fun http(url: String, method: String): SentryBreadcrumb {
            val breadcrumb = SentryBreadcrumb()
            breadcrumb.setType("http")
            breadcrumb.setCategory("http")
            breadcrumb.setData("url", url)
            breadcrumb.setData("method", method.uppercase())
            return breadcrumb
        }

        actual fun http(url: String, method: String, code: Int?): SentryBreadcrumb {
            val breadcrumb = http(url, method)
            if (code != null) {
                breadcrumb.setData("status_code", code)
            }
            return breadcrumb
        }

        actual fun navigation(from: String, to: String): SentryBreadcrumb {
            val breadcrumb = SentryBreadcrumb()
            breadcrumb.setCategory("navigation")
            breadcrumb.setType("navigation")
            breadcrumb.setData("from", from)
            breadcrumb.setData("to", to)
            return breadcrumb
        }

        actual fun transaction(message: String): SentryBreadcrumb {
            val breadcrumb = SentryBreadcrumb()
            breadcrumb.setType("default")
            breadcrumb.setCategory("sentry.transaction")
            breadcrumb.setMessage(message)
            return breadcrumb
        }

        actual fun debug(message: String): SentryBreadcrumb {
            val breadcrumb = SentryBreadcrumb()
            breadcrumb.setType("debug")
            breadcrumb.setMessage(message)
            breadcrumb.setLevel(SentryLevel.DEBUG)
            return breadcrumb
        }

        actual fun error(message: String): SentryBreadcrumb {
            val breadcrumb = SentryBreadcrumb()
            breadcrumb.setType("error")
            breadcrumb.setMessage(message)
            breadcrumb.setLevel(SentryLevel.ERROR)
            return breadcrumb
        }

        actual fun info(message: String): SentryBreadcrumb {
            val breadcrumb = SentryBreadcrumb()
            breadcrumb.setType("info")
            breadcrumb.setMessage(message)
            breadcrumb.setLevel(SentryLevel.INFO)
            return breadcrumb
        }

        actual fun query(message: String): SentryBreadcrumb {
            val breadcrumb = SentryBreadcrumb()
            breadcrumb.setType("query")
            breadcrumb.setType(message)
            return breadcrumb
        }

        actual fun ui(category: String, message: String): SentryBreadcrumb {
            val breadcrumb = SentryBreadcrumb()
            breadcrumb.setType("default")
            breadcrumb.setCategory("ui.$category")
            breadcrumb.setMessage(message)
            return breadcrumb
        }

        actual fun userInteraction(subCategory: String, viewId: String?, viewClass: String?): SentryBreadcrumb {
            return userInteraction(subCategory, viewId, viewClass, emptyMap<String?, Any>())
        }

        actual fun userInteraction(subCategory: String, viewId: String?, viewClass: String?, additionalData: Map<String?, Any?>): SentryBreadcrumb {
            val breadcrumb = SentryBreadcrumb()
            breadcrumb.setType("user")
            breadcrumb.setCategory("ui.$subCategory")
            if (viewId != null) {
                breadcrumb.setData("view.id", viewId)
            }
            if (viewClass != null) {
                breadcrumb.setData("view.class", viewClass)
            }
            for ((key, value) in additionalData) {
                breadcrumb.getData().put(key, value)
            }
            breadcrumb.setLevel(SentryLevel.INFO)
            return breadcrumb
        }

    }

    actual fun setType(type: String) {
        breadcrumb.type = type
    }

    actual fun setCategory(category: String) {
        breadcrumb.category = category
    }

    actual fun setMessage(message: String) {
        breadcrumb.message = message
    }

    actual fun setData(key: String, value: Any) {
        breadcrumb.setData(key, value)
    }

    actual fun getData(): MutableMap<String?, Any?> {
        return breadcrumb.data
    }

    actual fun setLevel(level: SentryLevel) {
        breadcrumb.level = level.toAndroidSentryLevel()
    }
}
