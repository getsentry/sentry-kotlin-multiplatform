package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User
import kotlin.js.Json
import kotlin.js.json

internal class ScopeJsImpl(private val scope: dynamic) : ISentryScope {

    override var level: SentryLevel?
        set(value) {
            value?.let { scope.setLevel(level?.name) }
        }
        get() {
            return scope._level
        }

    override var user: User?
        set(value) {

        }
        get() {
            val map = scope.serialize()["user"] as? Map<String, String>?
            return map?.let { User.fromMap(map) }
        }

    override fun getContexts(): MutableMap<String, Any> {
        return HashMap()
    }

    override fun getTags(): MutableMap<String, String> {
        return HashMap()
    }

    override fun addBreadcrumb(breadcrumb: Breadcrumb) {
        val jsBreadcrumbJson = json("message" to breadcrumb.getMessage())
        jsBreadcrumbJson.add(json("level" to breadcrumb.getLevel()?.name))
        scope.addBreadcrumb(jsBreadcrumbJson)
    }

    override fun clearBreadcrumbs() {
        scope.clearBreadcrumbs()
    }

    private fun setContextForPrimitiveValues(key: String, value: Any) {
        scope.setContext(key, json(("value" to value)))
    }

    var jsonValue: Json? = null
    fun rec(key: String, value: Any?) {
        val nextMap = value as? Map<Any?, Any>
        if (nextMap == null) {
            if (jsonValue == null) {
                jsonValue = json(key to value)
            } else {
                jsonValue?.set(key, value)
            }
            return
        } else {
            nextMap.forEach {
                rec(it.key as String, it.value)
            }
        }
    }

    // needs to recursively build the json through map
    override fun setContext(key: String, value: Any) {
        try {
            (value as? Map<Any?, Any>)?.let {
                it
                rec(key, value)
                scope.setContext(key, jsonValue)
            }
        } catch (e: Throwable) {
            setContextForPrimitiveValues(key, value)
        }
    }

    override fun setContext(key: String, value: String) {
        setContextForPrimitiveValues(key, value)
    }

    override fun setContext(key: String, value: Boolean) {
        setContextForPrimitiveValues(key, value)
    }

    override fun setContext(key: String, value: Number) {
        setContextForPrimitiveValues(key, value)
    }

    override fun setContext(key: String, value: Char) {
        setContextForPrimitiveValues(key, value)
    }

    override fun setContext(key: String, value: Array<*>) {
        setContextForPrimitiveValues(key, value)
    }

    override fun setContext(key: String, value: Collection<*>) {
        setContextForPrimitiveValues(key, value.toList())
    }

    override fun removeContext(key: String) {
        scope.removeContextForKey(key)
    }

    override fun setTag(key: String, value: String) {
        scope.setTag(key, value)
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
