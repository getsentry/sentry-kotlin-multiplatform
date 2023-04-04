package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User

public abstract class SentryBaseEvent(public var eventId: SentryId = SentryId.EMPTY_ID) {

    // public val contexts = Contexts()
    public open var release: String? = null
    public open var environment: String? = null
    public open var platform: String? = null
    public open var user: User? = null
    public open var serverName: String? = null
    public open var dist: String? = null
    private var breadcrumbs: MutableList<Breadcrumb>? = null
    private var tags: MutableMap<String, String>? = null

    public fun getBreadcrumbs(): List<Breadcrumb>? = breadcrumbs

    public fun getTags(): Map<String, String>? = tags
    public fun getTag(key: String): String? = tags?.get(key)

    public fun removeTag(key: String) {
        tags?.remove(key)
    }

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

    public fun addBreadcrumb(message: String?) {
        addBreadcrumb(Breadcrumb(message = message))
    }
}
