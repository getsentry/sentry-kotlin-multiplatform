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

    actual override var level: SentryLevel? = null
        get() {
            return scope?.level?.toKMPSentryLevel()
        }

    actual override var user: SentryUser? = null
        get() {
            return scope?.user?.toKMPSentryUser()
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

    actual override fun getContexts(): Map<String, Any>? {
        return scope?.contexts as Map<String, Any>
    }

    actual override fun getTags(): Map<String, String>? {
        return scope?.tags
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
