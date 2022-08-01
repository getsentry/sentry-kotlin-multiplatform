package io.sentry.kotlin.multiplatform

import io.sentry.ScopeCallback
import io.sentry.kotlin.multiplatform.extensions.*

actual class SentryScope : ISentryScope {

    // We are directly modifying the Android SDK scope
    private var scope: AndroidSentryScope? = null

    private var _level: SentryLevel? = null
    actual override var level: SentryLevel? = null
        set(value) {
            field = value
            _level = value
        }
        get() {
            return scope?.level?.toKMPSentryLevel()
        }

    private var _user: SentryUser? = null
    actual override var user: SentryUser? = null
        set(value) {
            field = value
            _user = value
        }
        get() {
            return scope?.user?.toKMPSentryUser()
        }

    /**
     * Initializes this KMP Scope with the Android Scope
     */
    internal fun initWithAndroidScope(androidScope: AndroidSentryScope) {
        this.scope = androidScope
    }

    /**
     * Invokes the callback on this KMP scope which in turn modifies the Android scope
     */
    internal fun scopeConfiguration(kmpScopeCallback: SentryScopeCallback): ScopeCallback {
        return ScopeCallback {
            initWithAndroidScope(it)
            kmpScopeCallback.run(this)
            syncFields()
        }
    }

    /**
     * Synchronizes the member fields who have no explicit setters in this scope with the Android scope
     */
    internal fun syncFields() {
        scope?.user = _user?.toAndroidSentryUser()
        scope?.level = _level?.toAndroidSentryLevel()
    }

    actual override fun getContexts(): MutableMap<String, Any>? {
        return scope?.contexts
    }

    actual override fun getTags(): MutableMap<String, String>? {
        return scope?.tags
    }

    actual override fun addBreadcrumb(breadcrumb: SentryBreadcrumb) {
        val androidBreadcrumb = breadcrumb.toAndroidBreadcrumb()
        scope?.addBreadcrumb(androidBreadcrumb)
    }

    actual override fun clearBreadcrumbs() {
        scope?.clearBreadcrumbs()
    }

    actual override fun setContext(key: String, value: Any) {
        scope?.setContexts(key, value)
    }

    actual override fun setContext(key: String, value: Boolean) {
        scope?.setContexts(key, value)
    }

    actual override fun setContext(key: String, value: String) {
        scope?.setContexts(key, value)
    }

    actual override fun setContext(key: String, value: Number) {
        scope?.setContexts(key, value)
    }

    actual override fun setContext(key: String, value: Collection<*>) {
        scope?.setContexts(key, value)
    }

    actual override fun setContext(key: String, value: Array<Any>) {
        scope?.setContexts(key, value)
    }

    actual override fun setContext(key: String, value: Char) {
        scope?.setContexts(key, value)
    }

    actual override fun removeContext(key: String) {
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
