package io.sentry.kotlin.multiplatform.protocol

import io.sentry.kotlin.multiplatform.SentryLevel

actual class Breadcrumb actual constructor(breadcrumb: ISentryBreadcrumb?) :
    ISentryBreadcrumb {
    actual companion object {
        actual fun user(
            category: String,
            message: String
        ): Breadcrumb {
            TODO("Not yet implemented")
        }

        actual fun http(
            url: String,
            method: String
        ): Breadcrumb {
            TODO("Not yet implemented")
        }

        actual fun http(
            url: String,
            method: String,
            code: Int?
        ): Breadcrumb {
            TODO("Not yet implemented")
        }

        actual fun navigation(
            from: String,
            to: String
        ): Breadcrumb {
            TODO("Not yet implemented")
        }

        actual fun transaction(message: String): Breadcrumb {
            TODO("Not yet implemented")
        }

        actual fun debug(message: String): Breadcrumb {
            TODO("Not yet implemented")
        }

        actual fun error(message: String): Breadcrumb {
            TODO("Not yet implemented")
        }

        actual fun info(message: String): Breadcrumb {
            TODO("Not yet implemented")
        }

        actual fun query(message: String): Breadcrumb {
            TODO("Not yet implemented")
        }

        actual fun ui(
            category: String,
            message: String
        ): Breadcrumb {
            TODO("Not yet implemented")
        }

        actual fun userInteraction(
            subCategory: String,
            viewId: String?,
            viewClass: String?
        ): Breadcrumb {
            TODO("Not yet implemented")
        }

        actual fun userInteraction(
            subCategory: String,
            viewId: String?,
            viewClass: String?,
            additionalData: Map<String?, Any?>
        ): Breadcrumb {
            TODO("Not yet implemented")
        }
    }

    actual override fun setType(type: String?) {
    }

    actual override fun setCategory(category: String?) {
    }

    actual override fun setMessage(message: String?) {
    }

    actual override fun setData(key: String, value: Any) {
    }

    actual override fun setData(map: MutableMap<String, Any>) {
    }

    actual override fun setLevel(level: SentryLevel?) {
    }

    actual override fun getType(): String? {
        TODO("Not yet implemented")
    }

    actual override fun getCategory(): String? {
        TODO("Not yet implemented")
    }

    actual override fun getMessage(): String? {
        TODO("Not yet implemented")
    }

    actual override fun getData(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    actual override fun getLevel(): SentryLevel? {
        TODO("Not yet implemented")
    }

    actual override fun clear() {
    }

}
