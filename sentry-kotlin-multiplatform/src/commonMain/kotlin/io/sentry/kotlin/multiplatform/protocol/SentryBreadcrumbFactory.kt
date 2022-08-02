package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.SentryLevel

/**
 * The factory is responsible for creating Breadcrumb objects in SentryBreadcrumb
 * in order to avoid code duplication.
 *
 * This is only for internal usage and is not exposed to the user.
 */
internal object SentryBreadcrumbFactory {
    fun user(category: String, message: String): SentryBreadcrumb {
        val breadcrumb = SentryBreadcrumb()
        breadcrumb.setType("user")
        breadcrumb.setCategory(category)
        breadcrumb.setMessage(message)
        return breadcrumb
    }

    fun http(url: String, method: String): SentryBreadcrumb {
        val breadcrumb = SentryBreadcrumb()
        breadcrumb.setType("http")
        breadcrumb.setCategory("http")
        breadcrumb.setData("url", url)
        breadcrumb.setData("method", method.uppercase())
        return breadcrumb
    }

    fun http(url: String, method: String, code: Int?): SentryBreadcrumb {
        val breadcrumb = http(url, method)
        if (code != null) {
            breadcrumb.setData("status_code", code)
        }
        return breadcrumb
    }

    fun navigation(from: String, to: String): SentryBreadcrumb {
        val breadcrumb = SentryBreadcrumb()
        breadcrumb.setCategory("navigation")
        breadcrumb.setType("navigation")
        breadcrumb.setData("from", from)
        breadcrumb.setData("to", to)
        return breadcrumb
    }

    fun transaction(message: String): SentryBreadcrumb {
        val breadcrumb = SentryBreadcrumb()
        breadcrumb.setType("default")
        breadcrumb.setCategory("sentry.transaction")
        breadcrumb.setMessage(message)
        return breadcrumb
    }

    fun debug(message: String): SentryBreadcrumb {
        val breadcrumb = SentryBreadcrumb()
        breadcrumb.setType("debug")
        breadcrumb.setMessage(message)
        breadcrumb.setLevel(SentryLevel.DEBUG)
        return breadcrumb
    }

    fun error(message: String): SentryBreadcrumb {
        val breadcrumb = SentryBreadcrumb()
        breadcrumb.setType("error")
        breadcrumb.setMessage(message)
        breadcrumb.setLevel(SentryLevel.ERROR)
        return breadcrumb
    }

    fun info(message: String): SentryBreadcrumb {
        val breadcrumb = SentryBreadcrumb()
        breadcrumb.setType("info")
        breadcrumb.setMessage(message)
        breadcrumb.setLevel(SentryLevel.INFO)
        return breadcrumb
    }

    fun query(message: String): SentryBreadcrumb {
        val breadcrumb = SentryBreadcrumb()
        breadcrumb.setType("query")
        breadcrumb.setMessage(message)
        return breadcrumb
    }

    fun ui(category: String, message: String): SentryBreadcrumb {
        val breadcrumb = SentryBreadcrumb()
        breadcrumb.setType("default")
        breadcrumb.setCategory("ui.$category")
        breadcrumb.setMessage(message)
        return breadcrumb
    }

    fun userInteraction(subCategory: String, viewId: String?, viewClass: String?, additionalData: Map<String?, Any?>): SentryBreadcrumb {
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
            breadcrumb.getData()?.put(key, value)
        }
        breadcrumb.setLevel(SentryLevel.INFO)
        return breadcrumb
    }

    fun userInteraction(subCategory: String, viewId: String?, viewClass: String?): SentryBreadcrumb {
        return userInteraction(subCategory, viewId, viewClass, emptyMap<String?, Any>())
    }
}
