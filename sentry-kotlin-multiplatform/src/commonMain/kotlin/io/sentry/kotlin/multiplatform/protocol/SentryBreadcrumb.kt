package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.SentryLevel

expect class SentryBreadcrumb() {
    companion object {
        fun user(category: String, message: String): SentryBreadcrumb
        fun http(url: String, method: String): SentryBreadcrumb
        fun http(url: String, method: String, code: Int?): SentryBreadcrumb
        fun navigation(from: String, to: String): SentryBreadcrumb
        fun transaction(message: String): SentryBreadcrumb
        fun debug(message: String): SentryBreadcrumb
        fun error(message: String): SentryBreadcrumb
        fun info(message: String): SentryBreadcrumb
        fun query(message: String): SentryBreadcrumb
        fun ui(category: String, message: String): SentryBreadcrumb
        fun userInteraction(subCategory: String, viewId: String?, viewClass: String?): SentryBreadcrumb
        fun userInteraction(subCategory: String, viewId: String?, viewClass: String?, additionalData: Map<String?, Any?>): SentryBreadcrumb
    }

    fun setType(type: String)
    fun setCategory(category: String)
    fun setMessage(message: String)
    fun setData(key: String, value: Any)
    fun setData(map: Map<String, Any>)
    fun setLevel(level: SentryLevel)

    fun getType(): String?
    fun getCategory(): String?
    fun getMessage(): String?
    fun getData(): MutableMap<String?, Any?>?
    fun getLevel(): SentryLevel?
}

interface ISentryBreadcrumb {
    // TODO: chore
}