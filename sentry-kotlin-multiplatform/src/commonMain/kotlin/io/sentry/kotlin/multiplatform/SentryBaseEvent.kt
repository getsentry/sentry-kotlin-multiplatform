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
    protected var mutableContexts: Map<String, Any>? = null
    public open val contexts: Map<String, Any>? get() = mutableContexts
    private var mutableBreadcrumbs: MutableList<Breadcrumb>? = null
    public open var breadcrumbs: List<Breadcrumb>?
        get() = this.mutableBreadcrumbs
        set(value) {
            this.mutableBreadcrumbs = value?.toMutableList()
        }

    private var mutableTags: MutableMap<String, String>? = null
    public open var tags: Map<String, String>?
        get() = this.mutableTags
        set(value) {
            this.mutableTags = value?.toMutableMap()
        }

    public fun getTag(key: String): String? = this.mutableTags?.get(key)

    public fun removeTag(key: String) {
        this.mutableTags?.remove(key)
    }

    public fun setTag(key: String, value: String) {
        if (mutableTags == null) {
            this.mutableTags = HashMap()
        }
        this.mutableTags!![key] = value
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
