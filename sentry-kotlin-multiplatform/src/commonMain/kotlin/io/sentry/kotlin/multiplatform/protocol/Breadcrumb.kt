package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.SentryLevel

expect class Breadcrumb() {
    companion object {
        fun user(category: String, message: String): Breadcrumb
        fun http(url: String, method: String): Breadcrumb
        fun http(url: String, method: String, code: Int?): Breadcrumb
        fun navigation(from: String, to: String): Breadcrumb
        fun transaction(message: String): Breadcrumb
        fun debug(message: String): Breadcrumb
        fun error(message: String): Breadcrumb
        fun info(message: String): Breadcrumb
        fun query(message: String): Breadcrumb
        fun ui(category: String, message: String): Breadcrumb
        fun userInteraction(subCategory: String, viewId: String?, viewClass: String?): Breadcrumb
        fun userInteraction(subCategory: String, viewId: String?, viewClass: String?, additionalData: Map<String?, Any?>): Breadcrumb
    }

    fun setType(type: String?)
    fun setCategory(category: String?)
    fun setMessage(message: String?)
    fun setData(key: String, value: Any)
    fun setData(map: MutableMap<String, Any>)
    fun setLevel(level: SentryLevel?)

    fun getType(): String?
    fun getCategory(): String?
    fun getMessage(): String?
    fun getData(): MutableMap<String, Any>
    fun getLevel(): SentryLevel?
}

interface ISentryBreadcrumb {
    // TODO: chore
}
