package io.sentry.kotlin.multiplatform

import io.sentry.ScopeCallback
import io.sentry.kotlin.multiplatform.extensions.toAndroidBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toAndroidSentryLevel
import io.sentry.util.CollectionUtils

actual class SentryScope : ISentryScope {

    // We are directly modifying the Android SDK scope
    private var scope: AndroidSentryScope? = null

    /**
     * This initializes the SentryScope wrapper with the Android scope and invokes the callback
     * on this KMP scope which in turn modifies the Android scope
     *
     */
    fun scopeConfiguration(kmpScopeCallback: SentryScopeCallback): ScopeCallback {
        return ScopeCallback {
            this.scope = it
            kmpScopeCallback.run(this)
        }
    }

    actual override fun addBreadcrumb(breadcrumb: SentryBreadcrumb) {
        val androidBreadcrumb = breadcrumb.toAndroidBreadcrumb()
        scope?.addBreadcrumb(androidBreadcrumb)
    }

    actual override fun clearBreadcrumbs() {
        scope?.clearBreadcrumbs()
    }

    actual override fun setUser(user: SentryUser) {
        val androidUser = AndroidSentryUser()
        androidUser.id = user.id
        androidUser.username = user.username
        androidUser.email = user.email
        androidUser.ipAddress = user.ipAddress
        androidUser.others = CollectionUtils.newConcurrentHashMap(user.other)
        androidUser.unknown = CollectionUtils.newConcurrentHashMap(user.unknown)
        scope?.user = androidUser
    }

    actual override fun setLevel(level: SentryLevel) {
        scope?.level = level.toAndroidSentryLevel()
    }

    actual override fun setContext(key: String, value: Any) {
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

    actual override override fun removeContext(key: String) {
        scope?.removeContexts(key)
    }

    actual override fun setTag(key: String, value: String) {
        scope?.setTag(key, value)
    }

    actual override fun removeTag(key: String) {
        scope?.removeTag(key)
    }

    actual override fun setExtra(key: String, value: String) {
        scope?.setExtra(key, value)
    }

    actual override fun removeExtra(key: String) {
        scope?.removeExtra(key)
    }

    actual override fun clear() {
        scope?.clear()
    }
}
