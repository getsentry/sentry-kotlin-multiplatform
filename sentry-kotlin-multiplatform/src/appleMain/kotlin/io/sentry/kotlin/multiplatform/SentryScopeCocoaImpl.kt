package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.*
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User
import platform.Foundation.NSDictionary
import platform.Foundation.allKeys

class SentryScopeCocoaImpl(private val scope: CocoaScope) : ISentryScope {

    override var level: SentryLevel?
        set(value) {
            value?.let { scope.setLevel(it.toCocoaSentryLevel()) }
        }
        get() {
            val level = scope.serialize()["level"]
            level?.let {
                val levelString = (level as String).uppercase()
                return SentryLevel.valueOf(levelString)
            }
            return null
        }

    override var user: User?
        set(value) {
            scope.setUser(value?.toCocoaUser())
        }
        get() {
            val map = scope.serialize()["user"] as Map<String, String>?
            map?.let { return userFromMap(map) }
            return null
        }

    private fun userFromMap(map: Map<String, String>): User {
        val user = User()
        user.email = map["email"] as String
        user.username = map["username"] as String
        user.id = map["id"] as String
        user.ipAddress = map["ip_address"] as String
        return user
    }

    override fun getContexts(): MutableMap<String, Any> {
        val context = scope.serialize()["context"]
        context?.let {
            val dict = context as NSDictionary
            val keys = dict.allKeys
            val map: MutableMap<String, Any> = HashMap()
            for (key in keys) {
                dict.objectForKey(key)?.let { map.put(key as String, it) }
            }
            return map
        }
        return HashMap()
    }

    override fun getTags(): MutableMap<String, String> {
        val tags = scope.serialize()["tags"]
        tags?.let {
            val dict = tags as NSDictionary
            val keys = dict.allKeys
            val map: MutableMap<String, String> = HashMap()
            for (key in keys) {
                map.put(key as String, dict.objectForKey(key) as String)
            }
            return map
        }
        return HashMap()
    }

    override fun addBreadcrumb(breadcrumb: Breadcrumb) {
        scope.addBreadcrumb(breadcrumb.toCocoaBreadcrumb())
    }

    override fun clearBreadcrumbs() {
        scope.clearBreadcrumbs()
    }

    private fun setContextForAnyValue(key: String, value: Any) {
        scope.setContextValue(mapOf("value" to value), key)
    }

    override fun setContext(key: String, value: Any) {
        try {
            scope.setContextValue(value as Map<Any?, Any>, key)
        } catch (e: Throwable) {
            setContextForAnyValue(key, value)
        }
    }

    override fun setContext(key: String, value: String) {
        setContextForAnyValue(key, value)
    }

    override fun setContext(key: String, value: Boolean) {
        setContextForAnyValue(key, value)
    }

    override fun setContext(key: String, value: Number) {
        setContextForAnyValue(key, value)
    }

    override fun setContext(key: String, value: Char) {
        setContextForAnyValue(key, value)
    }

    override fun setContext(key: String, value: Array<Any>) {
        setContextForAnyValue(key, value)
    }

    override fun setContext(key: String, value: Collection<*>) {
        setContextForAnyValue(key, value)
    }

    override fun removeContext(key: String) {
        scope.removeContextForKey(key)
    }

    override fun setTag(key: String, value: String) {
        scope.setTagValue(value, key)
    }

    override fun removeTag(key: String) {
        scope.removeTagForKey(key)
    }

    override fun setExtra(key: String, value: String) {
        scope.setExtraValue(value, key)
    }

    override fun removeExtra(key: String) {
        scope.removeExtraForKey(key)
    }

    override fun clear() {
        scope.clear()
    }
}
