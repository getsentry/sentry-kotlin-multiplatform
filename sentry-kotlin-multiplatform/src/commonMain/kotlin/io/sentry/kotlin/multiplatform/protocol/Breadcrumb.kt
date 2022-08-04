package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.SentryLevel

expect class Breadcrumb(breadcrumb: ISentryBreadcrumb? = null) : ISentryBreadcrumb {
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

    override fun setType(type: String?)
    override fun setCategory(category: String?)
    override fun setMessage(message: String?)
    override fun setData(key: String, value: Any)
    override fun setData(map: MutableMap<String, Any>)
    override fun setLevel(level: SentryLevel?)

    override fun getType(): String?
    override fun getCategory(): String?
    override fun getMessage(): String?
    override fun getData(): MutableMap<String, Any>
    override fun getLevel(): SentryLevel?

    override fun clear()
}

interface ISentryBreadcrumb {

    /**
     * Set's the breadcrumb's type
     *
     * @param type The type
     */
    fun setType(type: String?)

    /**
     * Set's the breadcrumb's type
     *
     * @param category The category
     */
    fun setCategory(category: String?)

    /**
     * Set's the breadcrumb's type
     *
     * @param message The message
     */
    fun setMessage(message: String?)

    /**
     * Set's the breadcrumb's data with key, value
     *
     * @param key The key
     * @param value The value
     */
    fun setData(key: String, value: Any)

    /**
     * Set's the breadcrumb's data with a map
     *
     * @param map The map
     */
    fun setData(map: MutableMap<String, Any>)

    /**
     * Set's the breadcrumb's level
     *
     * @param level The level
     */
    fun setLevel(level: SentryLevel?)

    /** Returns the breadcrumb's type */
    fun getType(): String?

    /** Returns the breadcrumb's category */
    fun getCategory(): String?

    /** Returns the breadcrumb's message */
    fun getMessage(): String?

    /** Returns the breadcrumb's data */
    fun getData(): MutableMap<String, Any>

    /** Returns the breadcrumb's level */
    fun getLevel(): SentryLevel?

    /** Clears the breadcrumb and returns it to the default state */
    fun clear()
}
