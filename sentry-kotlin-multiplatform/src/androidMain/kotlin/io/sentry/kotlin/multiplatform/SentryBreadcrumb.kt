package io.sentry.kotlin.multiplatform

import io.sentry.Breadcrumb
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKMPSentryLevel

actual class SentryBreadcrumb {

    private var breadcrumb: Breadcrumb = Breadcrumb()

    actual companion object {
        actual fun user(category: String, message: String): SentryBreadcrumb {
            return SentryBreadcrumbFactory.user(category, message)
        }

        actual fun http(url: String, method: String): SentryBreadcrumb {
            return SentryBreadcrumbFactory.http(url, method)
        }

        actual fun http(url: String, method: String, code: Int?): SentryBreadcrumb {
            return SentryBreadcrumbFactory.http(url, method, code)
        }

        actual fun navigation(from: String, to: String): SentryBreadcrumb {
            return SentryBreadcrumbFactory.navigation(from, to)
        }

        actual fun transaction(message: String): SentryBreadcrumb {
            return SentryBreadcrumbFactory.transaction(message)
        }

        actual fun debug(message: String): SentryBreadcrumb {
            return SentryBreadcrumbFactory.debug(message)
        }

        actual fun error(message: String): SentryBreadcrumb {
            return SentryBreadcrumbFactory.error(message)
        }

        actual fun info(message: String): SentryBreadcrumb {
            return SentryBreadcrumbFactory.info(message)
        }

        actual fun query(message: String): SentryBreadcrumb {
            return SentryBreadcrumbFactory.query(message)
        }

        actual fun ui(category: String, message: String): SentryBreadcrumb {
            return SentryBreadcrumbFactory.ui(category, message)
        }

        actual fun userInteraction(subCategory: String, viewId: String?, viewClass: String?): SentryBreadcrumb {
            return SentryBreadcrumbFactory.userInteraction(subCategory, viewId, viewClass)
        }

        actual fun userInteraction(subCategory: String, viewId: String?, viewClass: String?, additionalData: Map<String?, Any?>): SentryBreadcrumb {
            return SentryBreadcrumbFactory.userInteraction(subCategory, viewId, viewClass, additionalData)
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

    actual fun getData(): MutableMap<String?, Any?>? {
        return breadcrumb.data
    }

    actual fun setLevel(level: SentryLevel) {
        breadcrumb.level = level.toAndroidSentryLevel()
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
        return breadcrumb.level?.toKMPSentryLevel()
    }

    actual fun setUnknown(unknown: MutableMap<String?, Any?>?) {
        breadcrumb.unknown = unknown
    }

    actual fun getUnknown(): MutableMap<String?, Any?>? {
        return breadcrumb.unknown
    }
}
