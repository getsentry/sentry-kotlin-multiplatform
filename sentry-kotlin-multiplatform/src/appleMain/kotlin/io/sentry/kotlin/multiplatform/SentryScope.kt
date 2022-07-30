package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import cocoapods.Sentry.SentryScope as CocoaScope
import cocoapods.Sentry.SentryUser as CocoaUser

actual class SentryScope {

    // We are directly modifying the Cocoa SDK scope
    private var scope: CocoaScope? = null

    /**
     * This initalizes the SentryScope wrapper with the Cocoa scope and invokes the callback
     * on this KMP scope which in turn modifies the Cocoa scope
     *
     */
    fun scopeConfiguration(kmpScopeCallback: SentryScopeCallback): (CocoaScope?) -> Unit {
        return {
            this.scope = it
            kmpScopeCallback.run(this)
        }
    }

    actual fun setUser(user: SentryUser) {
        val cocoaUser = CocoaUser()
        cocoaUser.userId = user.id
        cocoaUser.username = user.username
        cocoaUser.email = user.email
        cocoaUser.ipAddress = user.ipAddress
        scope?.setUser(cocoaUser)
    }

    actual fun setLevel(level: SentryLevel) {
        scope?.setLevel(level.toCocoaSentryLevel())
    }

    actual fun setContext(key: String, value: Any) {
        try {
            scope?.setContextValue(value as Map<Any?, Any>, key)
        } catch (e: Throwable) {
            val map = HashMap<Any?, Any>()
            map.put("value", value)
            scope?.setContextValue(map, key)
        }
    }

    actual fun removeContext(key: String) {
        scope?.removeContextForKey(key)
    }

    actual fun setTag(key: String, value: String) {
        scope?.setTagValue(value, key)
    }

    actual fun removeTag(key: String) {
        scope?.removeTagForKey(key)
    }

    actual fun setExtra(key: String, value: String) {
        scope?.setExtraValue(value, key)
    }

    actual fun removeExtra(key: String) {
        scope?.removeExtraForKey(key)
    }

    actual fun clear() {
        scope?.clear()
    }
}
