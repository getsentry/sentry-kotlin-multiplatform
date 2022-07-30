package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentryBreadcrumb as CocoaBreadcrumb

actual class SentryBreadcrumb {

    private var breadcrumb: CocoaBreadcrumb = CocoaBreadcrumb()

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
            TODO("Not yet implemented")
        }

        actual fun navigation(from: String, to: String): SentryBreadcrumb {
            TODO("Not yet implemented")
        }

        actual fun transaction(message: String): SentryBreadcrumb {
            TODO("Not yet implemented")
        }

        actual fun debug(message: String): SentryBreadcrumb {
            TODO("Not yet implemented")
        }

        actual fun error(message: String): SentryBreadcrumb {
            TODO("Not yet implemented")
        }

        actual fun info(message: String): SentryBreadcrumb {
            TODO("Not yet implemented")
        }

        actual fun query(message: String): SentryBreadcrumb {
            TODO("Not yet implemented")
        }

        actual fun ui(category: String, message: String): SentryBreadcrumb {
            TODO("Not yet implemented")
        }

        actual fun userInteraction(subCategory: String, viewId: String?, viewClass: String?): SentryBreadcrumb {
            TODO("Not yet implemented")
        }

        actual fun userInteraction(subCategory: String, viewId: String?, viewClass: String?, additionalData: Map<String?, Any?>): SentryBreadcrumb {
            TODO("Not yet implemented")
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
        // TODO: key, value to map
    }

    actual fun setLevel(level: SentryLevel) {
    }

    actual fun getData(): MutableMap<String?, Any?> {
        TODO("Not yet implemented")
    }
}
