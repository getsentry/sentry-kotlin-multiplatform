package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User

public abstract class SentryBaseEvent(public open var eventId: SentryId = SentryId.EMPTY_ID) {

    public open var release: String? = null
    public open var environment: String? = null
    public open var platform: String? = null
    public open var user: User? = null
    public open var serverName: String? = null
    public open var dist: String? = null
    protected var _contexts: Map<String, Any>? = null
    public open val contexts: Map<String, Any>? get() = _contexts
    private var _breadcrumbs: MutableList<Breadcrumb>? = null
    public open var breadcrumbs: List<Breadcrumb>?
        get() = _breadcrumbs
        set(value) {
            _breadcrumbs = value?.toMutableList()
        }

    private var _tags: MutableMap<String, String>? = null
    public open var tags: Map<String, String>?
        get() = _tags
        set(value) {
            _tags = value?.toMutableMap()
        }

    public fun getTag(key: String): String? = _tags?.get(key)

    public fun removeTag(key: String) {
        _tags?.remove(key)
    }

    public fun setTag(key: String, value: String) {
        if (_tags == null) {
            _tags = HashMap()
        }
        _tags!![key] = value
    }

    public fun addBreadcrumb(breadcrumb: Breadcrumb) {
        if (_breadcrumbs == null) {
            _breadcrumbs = mutableListOf()
        }
        _breadcrumbs?.add(breadcrumb)
    }

    public fun addBreadcrumb(message: String?) {
        addBreadcrumb(Breadcrumb(message = message))
    }
}
