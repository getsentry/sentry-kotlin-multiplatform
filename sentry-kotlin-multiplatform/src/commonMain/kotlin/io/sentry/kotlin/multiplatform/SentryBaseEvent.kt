package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User

/** Base class for all Sentry events. */
public abstract class SentryBaseEvent(public open var eventId: SentryId = SentryId.EMPTY_ID) {

    public open var release: String? = null
    public open var environment: String? = null
    public open var platform: String? = null
    public open var user: User? = null
    public open var serverName: String? = null
    public open var dist: String? = null

    /** This is not thread-safe */
    public open val contexts: Map<String, Any>? get() = mutableContexts
    internal var mutableContexts: Map<String, Any>? = null

    /** This is not thread-safe */
    public open val breadcrumbs: List<Breadcrumb>? get() = mutableBreadcrumbs
    internal var mutableBreadcrumbs: MutableList<Breadcrumb>? = null

    /** This is not thread-safe */
    public open val tags: Map<String, String>? get() = mutableTags
    internal var mutableTags: MutableMap<String, String>? = null

    public fun getTag(key: String): String? = mutableTags?.get(key)

    public fun removeTag(key: String) {
        mutableTags?.remove(key)
    }

    public fun setTag(key: String, value: String) {
        if (mutableTags == null) {
            mutableTags = HashMap()
        }
        mutableTags?.set(key, value)
    }

    public fun addBreadcrumb(breadcrumb: Breadcrumb) {
        if (mutableBreadcrumbs == null) {
            mutableBreadcrumbs = mutableListOf()
        }
        mutableBreadcrumbs?.add(breadcrumb)
    }

    public fun addBreadcrumb(message: String?) {
        addBreadcrumb(Breadcrumb(message = message))
    }
}
