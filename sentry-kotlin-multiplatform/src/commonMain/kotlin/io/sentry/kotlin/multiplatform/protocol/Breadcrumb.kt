package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.SentryLevel

data class Breadcrumb constructor(
    override var type: String? = null,
    override var category: String? = null,
    override var message: String? = null,
    override var level: SentryLevel? = null,
    private var data: MutableMap<String, Any>? = null,
) : ISentryBreadcrumb {

    companion object {
        fun user(category: String, message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.category = category
                this.message = message
                this.type = "user"
            }
        }

        fun http(url: String, method: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "http"
                this.category = "http"
                this.setData("url", url)
                this.setData("method", method.uppercase())
            }
        }

        fun http(url: String, method: String, code: Int?): Breadcrumb {
            return http(url, method).apply {
                code?.let { this.setData("status_code", code) }
            }
        }

        fun navigation(from: String, to: String): Breadcrumb {
            return Breadcrumb().apply {
                this.category = "navigation"
                this.type = "navigation"
                this.setData("from", from)
                this.setData("to", to)
            }
        }

        fun transaction(message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "default"
                this.category = "sentry.transaction"
                this.message = message
            }
        }

        fun debug(message: String): Breadcrumb {
            val breadcrumb = Breadcrumb().apply {
                this.type = "debug"
                this.message = message
                this.level = SentryLevel.DEBUG
            }
            return breadcrumb
        }

        fun error(message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "error"
                this.message = message
                this.level = SentryLevel.ERROR
            }
        }

        fun info(message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "info"
                this.message = message
                this.level = SentryLevel.INFO
            }
        }

        fun query(message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "query"
                this.message = message
            }
        }

        fun ui(category: String, message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "default"
                this.category = "ui.$category"
                this.message = message
            }
        }

        fun userInteraction(
            subCategory: String,
            viewId: String?,
            viewClass: String?,
            additionalData: Map<String?, Any?>
        ): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "user"
                this.category = "ui.$subCategory"
                this.level = SentryLevel.INFO
                viewId?.let { this.setData("view.id", it) }
                viewClass?.let { this.setData("view.class", viewClass) }
                for ((key, value) in additionalData) {
                    if (key != null && value != null) {
                        this.setData(key, value)
                    }
                }
            }
        }

        fun userInteraction(subCategory: String, viewId: String?, viewClass: String?): Breadcrumb {
            return userInteraction(subCategory, viewId, viewClass, emptyMap<String?, Any>())
        }
    }

    override fun setData(key: String, value: Any) {
        if (data == null) data = mutableMapOf()
        data?.put(key, value)
    }

    override fun setData(map: MutableMap<String, Any>) {
        data = map
    }

    override fun getData(): MutableMap<String, Any>? {
        return data
    }

    override fun clear() {
        data = null
        level = null
        category = null
        type = null
        message = null
    }
}


interface ISentryBreadcrumb {

    /** The breadcrumb's level */
    var level: SentryLevel?

    /** The breadcrumb's type */
    var type: String?

    /** The breadcrumb's message */
    var message: String?

    /** The breadcrumb's category */
    var category: String?

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

    /** Returns the breadcrumb's data */
    fun getData(): MutableMap<String, Any>?

    /** Clears the breadcrumb and returns it to the default state */
    fun clear()
}
