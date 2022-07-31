package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import cocoapods.Sentry.SentryScope as CocoaScope
import cocoapods.Sentry.SentryUser as CocoaUser

actual class SentryScope : ISentryScope {

    // We are directly modifying the Cocoa SDK scope
    private var scope: CocoaScope? = null

    /*
    We keep track of multiple properties as well on the Cocoa platform because the SDK
    doesn't have `get` access to properties
    */
    private var context: MutableMap<Any?, Any>? = HashMap()
    private var level: SentryLevel? = null
    private var user: SentryUser? = null

    /**
     * Initializies this KMP Scope with the Cocoa Scope
     */
    fun initWithCocoaScope(cocoaScope: CocoaScope) {
        this.scope = cocoaScope
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
        }
    }

    actual override fun addBreadcrumb(breadcrumb: SentryBreadcrumb) {

    }


    actual override fun clearBreadcrumbs() {
        scope?.clearBreadcrumbs()
    }

    actual override fun getUser(): SentryUser? {
        return user
    }

    actual override fun getContext(): Map<String, Any>? {
        return context as Map<String, Any>?
    }

    actual override fun getLevel(): SentryLevel? {
        return level
    }

    actual override fun setUser(user: SentryUser) {
        val cocoaUser = CocoaUser()
        cocoaUser.userId = user.id
        cocoaUser.username = user.username
        cocoaUser.email = user.email
        cocoaUser.ipAddress = user.ipAddress
        scope?.setUser(cocoaUser)
    }

    actual override fun setLevel(level: SentryLevel) {
        scope?.setLevel(level.toCocoaSentryLevel())
    }

    actual override fun setContext(key: String, value: Any) {
        try {
            context?.put(key, value)
            scope?.setContextValue(value as Map<Any?, Any>, key)
        } catch (e: Throwable) {
            val map = HashMap<Any?, Any>()
            map.put("value", value)
            context?.put(key, map)
            scope?.setContextValue(map, key)
        }
    }

    actual override fun removeContext(key: String) {
        context?.remove(key)
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
        context?.clear()
        scope?.clear()
    }
}
