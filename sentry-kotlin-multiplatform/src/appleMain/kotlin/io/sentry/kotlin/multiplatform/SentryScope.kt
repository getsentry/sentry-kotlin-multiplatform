package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryUser
import cocoapods.Sentry.SentryScope as CocoaScope

actual class SentryScope : ISentryScope {

    // We are directly modifying the Cocoa SDK scope
    private var scope: CocoaScope? = null

    private var serializedMap: Map<String, Any>? = HashMap()

    actual override var level: SentryLevel? = null
        get() {
            if (serializedMap?.get("level") != null) {
                val levelString = (serializedMap as String).uppercase()
                return SentryLevel.valueOf(levelString)

            }
            return null
        }

    actual override var user: SentryUser? = null
        get() {
            val userMap = serializedMap?.get("user") as Map<String, String>?
            if (userMap != null) {
                val sentryUser = SentryUser()
                sentryUser.email = userMap["email"] as String
                sentryUser.username = userMap["username"] as String
                sentryUser.id = userMap["id"] as String
                sentryUser.ipAddress = userMap["ip_address"] as String
                return sentryUser
            }
            return null
        }

    /**
     * Initializies this KMP Scope with the Cocoa Scope
     *
     * @param cocoaScope: The Cocoa SDK scope
     */
    fun initWithCocoaScope(cocoaScope: CocoaScope) {
        this.scope = cocoaScope
        this.serializedMap = cocoaScope.serialize() as Map<String, Any>?
    }

    /**
     * This initializes the SentryScope wrapper with the Cocoa scope and invokes the callback
     * on this KMP scope which in turn modifies the Cocoa scope
     *
     */
    fun scopeConfiguration(kmpScopeCallback: SentryScopeCallback): (CocoaScope?) -> Unit {
        return {
            initWithCocoaScope(it!!)
            kmpScopeCallback.run(this)
            syncFields()
        }
    }

    /**
     * Synchronizes the fields in this scope with the Cocoa scope
     */
    private fun syncFields() {
        if (user != null) {
            scope?.setUser(user?.toCocoaSentryUser())
        }
        if (level != null) {
            scope?.setLevel(level!!.toCocoaSentryLevel())
        }
    }

    actual override fun addBreadcrumb(breadcrumb: SentryBreadcrumb) {

    }

    actual override fun clearBreadcrumbs() {
        scope?.clearBreadcrumbs()
    }

    actual override fun getContexts(): Map<String, Any>? {
        return serializedMap?.get("context") as Map<String, Any>?
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

    actual override fun getTags(): Map<String, String>? {
        return serializedMap?.get("tags") as Map<String, String>?
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
