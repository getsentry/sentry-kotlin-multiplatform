package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User

public abstract class SentryBaseEvent(public var eventId: SentryId = SentryId.EMPTY_ID) {

    // public val contexts = Contexts()
    public var tags: MutableMap<String, String>? = null
        set(value) {
            field = value?.let { HashMap(it) }
        }
    public var release: String? = null
    public var environment: String? = null
    public var platform: String? = null
    public var user: User? = null
    protected var throwable: Throwable? = null
    public var serverName: String? = null
    public var dist: String? = null
    private var breadcrumbs: MutableList<Breadcrumb>? = null
        set(value) {
            field = value?.let { ArrayList(it) }
        }
    private var extra: MutableMap<String, Any>? = null
        set(value) {
            field = value?.let { HashMap(it) }
        }

    public fun removeTag(key: String) {
        tags?.remove(key)
    }

    public fun getTag(key: String): String? = tags?.get(key)

    public fun setTag(key: String, value: String) {
        if (tags == null) {
            tags = HashMap()
        }
        tags!![key] = value
    }

    public fun addBreadcrumb(breadcrumb: Breadcrumb) {
        if (breadcrumbs == null) {
            breadcrumbs = mutableListOf()
        }
        breadcrumbs?.add(breadcrumb)
    }

    public fun setExtra(key: String, value: Any) {
        if (extra == null) {
            extra = HashMap()
        }
        extra!![key] = value
    }

    public fun removeExtra(key: String) {
        extra?.remove(key)
    }

    public fun getExtra(key: String): Any? = extra?.get(key)

    public fun addBreadcrumb(message: String?) {
        addBreadcrumb(Breadcrumb(message = message))
    }
}
