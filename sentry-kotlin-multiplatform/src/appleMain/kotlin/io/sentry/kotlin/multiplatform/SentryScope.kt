package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryUser
import platform.Foundation.NSDictionary
import platform.Foundation.allKeys
import cocoapods.Sentry.SentryScope as CocoaScope

actual class SentryScope : ISentryScope {

    // We are directly modifying the Cocoa SDK scope
    private var scope: CocoaScope? = null

    private var _level: SentryLevel? = null
    actual override var level: SentryLevel? = null
        set(value) {
            field = value
            _level = value
        }
        get() {
            val levelMap = scope?.serialize()?.get("level")
            if (levelMap != null) {
                val levelString = (levelMap as String).uppercase()
                return SentryLevel.valueOf(levelString)
            }
            return _level
        }

    private var _user: SentryUser? = null
    actual override var user: SentryUser? = null
        set(value) {
            field = value
            _user = value
        }
        get() {
            val userMap = scope?.serialize()?.get("user") as Map<String, String>?
            if (userMap != null) {
                val sentryUser = SentryUser()
                sentryUser.email = userMap["email"] as String
                sentryUser.username = userMap["username"] as String
                sentryUser.id = userMap["id"] as String
                sentryUser.ipAddress = userMap["ip_address"] as String
                return sentryUser
            }
            return _user
        }

    /**
     * Initializes this KMP Scope with the Cocoa Scope
     *
     * @param cocoaScope: The Cocoa SDK scope
     */
    internal fun initWithCocoaScope(cocoaScope: CocoaScope) {
        this.scope = cocoaScope
    }

    /**
     * This initializes the SentryScope wrapper with the Cocoa scope and invokes the callback
     * on this KMP scope which in turn modifies the Cocoa scope
     */
    internal fun scopeConfiguration(kmpScopeCallback: SentryScopeCallback): (CocoaScope?) -> Unit {
        return {
            initWithCocoaScope(it!!)
            kmpScopeCallback.run(this)
            syncFields()
        }
    }

    /**
     * Synchronizes the member fields who have no explicit setters in this scope with the Android scope
     */
    internal fun syncFields() {
        scope?.setUser(_user?.toCocoaSentryUser())
        scope?.setLevel(_level!!.toCocoaSentryLevel())
    }

    actual override fun getContexts(): MutableMap<String, Any>? {
        if (scope?.serialize()?.get("context") != null) {
            val dict = scope?.serialize()?.get("context") as NSDictionary
            val keys = dict.allKeys
            val map: MutableMap<String, Any> = HashMap()
            for (key in keys) {
                dict.objectForKey(key)?.let { map.put(key as String, it) }
            }
            return map
        }
        return HashMap()
    }

    actual override fun getTags(): MutableMap<String, String>? {
        if (scope?.serialize()?.get("tags") != null) {
            val dict = scope?.serialize()?.get("tags") as NSDictionary
            val keys = dict.allKeys
            val map: MutableMap<String, String> = HashMap()
            for (key in keys) {
                map.put(key as String, dict.objectForKey(key) as String)
            }
            return map
        }
        return HashMap()
    }

    actual override fun addBreadcrumb(breadcrumb: SentryBreadcrumb) {
        scope?.addBreadcrumb(breadcrumb.toCocoaBreadcrumb())
    }

    actual override fun clearBreadcrumbs() {
        scope?.clearBreadcrumbs()
    }

    private fun setContextForAnyValue(key: String, value: Any) {
        val map = HashMap<Any?, Any>()
        map.put("value", value)
        scope?.setContextValue(map, key)
    }

    actual override fun setContext(key: String, value: Any) {
        try {
            scope?.setContextValue(value as Map<Any?, Any>, key)
        } catch (e: Throwable) {
            setContextForAnyValue(key, value)
        }
    }

    actual override fun setContext(key: String, value: String) {
        setContextForAnyValue(key, value)
    }

    actual override fun setContext(key: String, value: Boolean) {
        setContextForAnyValue(key, value)
    }

    actual override fun setContext(key: String, value: Number) {
        setContextForAnyValue(key, value)
    }

    actual override fun setContext(key: String, value: Char) {
        setContextForAnyValue(key, value)
    }

    actual override fun setContext(key: String, value: Array<Any>) {
        setContextForAnyValue(key, value)
    }

    actual override fun setContext(key: String, value: Collection<*>) {
        setContextForAnyValue(key, value)
    }

    actual override fun removeContext(key: String) {
        scope?.removeContextForKey(key)
    }

    actual override fun setTag(key: String, value: String) {
        scope?.setTagValue(value, key)
    }

    actual override fun removeTag(key: String) {
        scope?.removeTagForKey(key)
    }

    actual override fun setExtra(key: String, value: String) {
        scope?.setExtraValue(value, key)
    }

    actual override fun removeExtra(key: String) {
        scope?.removeExtraForKey(key)
    }

    actual override fun clear() {
        user = null
        level = null
        scope?.clear()
    }
}
