package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.SentryLevel

/**
 * A single breadcrumb.
 *
 * Breadcrumbs are used to create a trail of events that happened prior to an issue.
 */
public data class Breadcrumb(
    /** The breadcrumb's level */
    var level: SentryLevel? = null,

    /** The breadcrumb's type */
    var type: String? = null,

    /** The breadcrumb's message */
    var message: String? = null,

    /** The breadcrumb's category */
    var category: String? = null,

    private var data: MutableMap<String, Any>? = null
) {

    public companion object {
        public fun user(category: String, message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.category = category
                this.message = message
                this.type = "user"
            }
        }

        public fun http(url: String, method: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "http"
                this.category = "http"
                this.setData("url", url)
                this.setData("method", method.uppercase())
            }
        }

        public fun http(url: String, method: String, code: Int?): Breadcrumb {
            return http(url, method).apply {
                code?.let { this.setData("status_code", code) }
            }
        }

        public fun navigation(from: String, to: String): Breadcrumb {
            return Breadcrumb().apply {
                this.category = "navigation"
                this.type = "navigation"
                this.setData("from", from)
                this.setData("to", to)
            }
        }

        public fun transaction(message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "default"
                this.category = "sentry.transaction"
                this.message = message
            }
        }

        public fun debug(message: String): Breadcrumb {
            val breadcrumb = Breadcrumb().apply {
                this.type = "debug"
                this.message = message
                this.level = SentryLevel.DEBUG
            }
            return breadcrumb
        }

        public fun error(message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "error"
                this.message = message
                this.level = SentryLevel.ERROR
            }
        }

        public fun info(message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "info"
                this.message = message
                this.level = SentryLevel.INFO
            }
        }

        public fun query(message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "query"
                this.message = message
            }
        }

        public fun ui(category: String, message: String): Breadcrumb {
            return Breadcrumb().apply {
                this.type = "default"
                this.category = "ui.$category"
                this.message = message
            }
        }

        public fun userInteraction(
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

        public fun userInteraction(
            subCategory: String,
            viewId: String?,
            viewClass: String?
        ): Breadcrumb {
            return userInteraction(subCategory, viewId, viewClass, emptyMap<String?, Any>())
        }
    }

    /**
     * Set's the breadcrumb's data with key, value
     *
     * @param key The key
     * @param value The value
     */
    public fun setData(key: String, value: Any) {
        if (data == null) data = mutableMapOf()
        data?.put(key, value)
    }

    /**
     * Set's the breadcrumb's data with a map
     *
     * @param map The map
     */
    public fun setData(map: MutableMap<String, Any>) {
        data = map
    }

    /** Returns the breadcrumb's data */
    public fun getData(): MutableMap<String, Any>? {
        return data
    }

    /** Clears the breadcrumb and returns it to the default state */
    public fun clear() {
        data = null
        level = null
        category = null
        type = null
        message = null
    }
}
