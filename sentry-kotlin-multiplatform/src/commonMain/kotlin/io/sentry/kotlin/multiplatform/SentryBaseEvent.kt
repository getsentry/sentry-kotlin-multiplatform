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
    protected var mutableContexts: Map<String, Any>? = null

    /** This is not thread-safe */
    public open val breadcrumbs: List<Breadcrumb>? get() = this.mutableBreadcrumbs
    protected var mutableBreadcrumbs: MutableList<Breadcrumb>? = null

    /** This is not thread-safe */
    public open val tags: Map<String, String>? get() = this.mutableTags
    protected var mutableTags: MutableMap<String, String>? = null

    public fun getTag(key: String): String? = this.mutableTags?.get(key)

    public fun removeTag(key: String) {
        this.mutableTags?.remove(key)
    }

    public fun setTag(key: String, value: String) {
        if (mutableTags == null) {
            this.mutableTags = HashMap()
        }
        this.mutableTags?.set(key, value)
    }

    public fun addBreadcrumb(breadcrumb: Breadcrumb) {
        if (mutableBreadcrumbs == null) {
            this.mutableBreadcrumbs = mutableListOf()
        }
        this.mutableBreadcrumbs?.add(breadcrumb)
    }

    public fun addBreadcrumb(message: String?) {
        this.addBreadcrumb(Breadcrumb(message = message))
    }
}
