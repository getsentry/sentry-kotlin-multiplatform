package io.sentry.kotlin.multiplatform

import io.sentry.ScopeCallback
import io.sentry.kotlin.multiplatform.extensions.*
import io.sentry.kotlin.multiplatform.protocol.SentryBreadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryUser

actual class SentryScope : ISentryScope {

    // We are directly modifying the Android SDK scope
    private var scope: AndroidSentryScope? = null

    actual override var level: SentryLevel?
        set(value) {
            scope?.level = value?.toAndroidSentryLevel()
        }
        get() {
            return scope?.level?.toKMPSentryLevel()
        }

    actual override var user: SentryUser?
        set(value) {
            value?.onFieldChanged = {
                scope?.user = value?.toAndroidSentryUser()
            }
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
    internal fun scopeConfiguration(kmpScopeCallback: (SentryScope) -> Unit): ScopeCallback {
        return ScopeCallback {
            initWithAndroidScope(it)
            kmpScopeCallback.invoke(this)
        }
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
