package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import cocoapods.Sentry.SentryScope as CocoaScope
import cocoapods.Sentry.SentryUser as User

actual class SentryScope {
    private var scope: CocoaScope? = null

    fun initWithScope(scope: CocoaScope) {
        this.scope = scope
    }

    actual fun setUser(user: SentryUser) {
        val cocoaUser = User()
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
            scope?.setContextValue(mapAnyContext(value), key)
        }
    }

    private fun mapAnyContext(value: Any): Map<Any?, Any> {
        val map = HashMap<Any?, Any>()
        map.put("value", value)
        return map
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
        // setExtra needs map
    }

    actual fun removeExtra(key: String) {
        scope?.removeExtraForKey(key)
    }

    actual fun clear() {
        scope?.clear()
    }
}
