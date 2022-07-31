package io.sentry.kotlin.multiplatform

import io.sentry.ScopeCallback
import io.sentry.kotlin.multiplatform.extensions.*

actual class SentryScope : ISentryScope {

    // We are directly modifying the Android SDK scope
    private var scope: AndroidSentryScope? = null

    /**
     * Initializies this KMP Scope with the Android Scope
     */
    fun initWithAndroidScope(androidScope: AndroidSentryScope) {
        this.scope = androidScope
    }

    /**
     * Invokes the callback on this KMP scope which in turn modifies the Android scope
     */
    fun scopeConfiguration(kmpScopeCallback: SentryScopeCallback): ScopeCallback {
        return ScopeCallback {
            initWithAndroidScope(it)
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
        scope?.user = user.toAndroidSentryUser()
    }

    actual override fun getUser(): SentryUser? {
        return scope?.user?.toKMPSentryUser()
    }

    actual override fun setLevel(level: SentryLevel) {
        scope?.level = level.toAndroidSentryLevel()
    }

    actual override fun getLevel(): SentryLevel? {
        return scope?.level?.toKMPSentryLevel()
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

    actual override fun removeContext(key: String) {
        scope?.removeContexts(key)
    }

    actual override fun getContext(): Map<String, Any>? {
        return scope?.contexts as Map<String, Any>
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
