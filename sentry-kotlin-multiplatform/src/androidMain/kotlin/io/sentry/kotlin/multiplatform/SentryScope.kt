package io.sentry.kotlin.multiplatform

import io.sentry.ScopeCallback
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryLevel
import io.sentry.protocol.User as AndroidUser
import io.sentry.util.CollectionUtils
import io.sentry.Scope as AndroidScope

actual class SentryScope {

    // We are directly modifying the Android SDK scope
    private var scope: AndroidScope? = null

    /**
     * This initalizes the SentryScope wrapper with the Android scope and invokes the callback
     * on this KMP scope which in turn modifies the Android scope
     *
     */
    fun scopeConfiguration(kmpScopeCallback: SentryScopeCallback): ScopeCallback {
        return ScopeCallback {
            this.scope = it
            kmpScopeCallback.run(this)
        }
    }

    actual fun addBreadcrumb(crumb: SentryBreadcrumb) {

    }

    actual fun clearBreadcrumbs() {

    }

    actual fun setUser(user: SentryUser) {
        val androidUser = AndroidUser()
        androidUser.id = user.id
        androidUser.username = user.username
        androidUser.email = user.email
        androidUser.ipAddress = user.ipAddress
        androidUser.others = CollectionUtils.newConcurrentHashMap(user.other)
        androidUser.unknown = CollectionUtils.newConcurrentHashMap(user.unknown)
        scope?.user = androidUser
    }

    actual fun setLevel(level: SentryLevel) {
        scope?.level = level.toAndroidSentryLevel()
    }

    actual fun setContext(key: String, value: Any) {
        if (value is String) {
            scope?.setContexts(key, value)
        }
        if (value is Boolean) {
            scope?.setContexts(key, value)
        }
        if (value is Number) {
            scope?.setContexts(key, value)
        }
        if (value is Char) {
            scope?.setContexts(key, value)
        }
        if (value is Collection<*>) {
            scope?.setContexts(key, value)
        }
        if (value is Array<*>) {
            scope?.setContexts(key, value as Array<Any>)
        }
    }

    actual fun removeContext(key: String) {
        scope?.removeContexts(key)
    }

    actual fun setTag(key: String, value: String) {
        scope?.setTag(key, value)
    }

    actual fun removeTag(key: String) {
        scope?.removeTag(key)
    }

    actual fun setExtra(key: String, value: String) {
        scope?.setExtra(key, value)
    }

    actual fun removeExtra(key: String) {
        scope?.removeExtra(key)
    }

    actual fun clear() {
        scope?.clear()
    }
}
