package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User

class SentryScope constructor(private val scope: ISentryScope) : ISentryScope {

    override var level: SentryLevel?
        set(value) {
            scope.level = value
        }
        get() {
            return scope.level
        }

    override var user: User?
        set(value) {
            scope.user = value
        }
        get() {
            return scope.user
        }

    override fun getContexts(): MutableMap<String, Any>? {
        return scope.getContexts()
    }

    override fun getTags(): MutableMap<String, String>? {
        return scope.getTags()
    }

    override fun addBreadcrumb(breadcrumb: Breadcrumb) {
        scope.addBreadcrumb(breadcrumb)
    }

    override fun clearBreadcrumbs() {
        scope.clearBreadcrumbs()
    }

    override fun setContext(key: String, value: Any) {
        scope.setContext(key, value)
    }

    override fun setContext(key: String, value: Boolean) {
        scope.setContext(key, value)
    }

    override fun setContext(key: String, value: String) {
        scope.setContext(key, value)
    }

    override fun setContext(key: String, value: Number) {
        scope.setContext(key, value)
    }

    override fun setContext(key: String, value: Collection<*>) {
        scope.setContext(key, value)
    }

    override fun setContext(key: String, value: Array<*>) {
        scope.setContext(key, value)
    }

    override fun setContext(key: String, value: Char) {
        scope.setContext(key, value)
    }

    override fun removeContext(key: String) {
        scope.removeContext(key)
    }

    override fun setTag(key: String, value: String) {
        scope.setTag(key, value)
    }

    override fun removeTag(key: String) {
        scope.removeTag(key)
    }

    override fun setExtra(key: String, value: String) {
        scope.setExtra(key, value)
    }

    override fun removeExtra(key: String) {
        scope.removeExtra(key)
    }

    override fun clear() {
        scope.clear()
    }
}

interface ISentryScope {

    /**
     * Returns the scope's tags
     */
    fun getTags(): MutableMap<String, String>?

    /**
     * Returns the Scope's contexts
     */
    fun getContexts(): MutableMap<String, Any>?

    /**
     * The Scope's user
     */
    var user: User?

    /**
     * The Scope's level
     */
    var level: SentryLevel?

    /**
     * Adds a breadcrumb to the breadcrumbs queue
     *
     * @param breadcrumb the breadcrumb
     */
    fun addBreadcrumb(breadcrumb: Breadcrumb)

    /** Clear all the breadcrumbs */
    fun clearBreadcrumbs()

    /**
     * Sets the Scope's contexts
     *
     * @param key the context key
     * @param value the context value
     */
    fun setContext(key: String, value: Any)

    /**
     * Sets the Scope's contexts
     *
     * @param key the context key
     * @param value the context value
     */
    fun setContext(key: String, value: Boolean)

    /**
     * Sets the Scope's context
     *
     * @param key the context key
     * @param value the context value
     */
    fun setContext(key: String, value: String)

    /**
     * Sets the Scope's context
     *
     * @param key the context key
     * @param value the context value
     */
    fun setContext(key: String, value: Number)

    /**
     * Sets the Scope's context
     *
     * @param key the context key
     * @param value the context value
     */
    fun setContext(key: String, value: Collection<*>)

    /**
     * Sets the Scope's context
     *
     * @param key the context key
     * @param value the context value
     */
    fun setContext(key: String, value: Array<*>)

    /**
     * Sets the Scope's context
     *
     * @param key the context key
     * @param value the context value
     */
    fun setContext(key: String, value: Char)

    /**
     * Removes a value from the Scope's contexts
     *
     * @param key the Key
     */
    fun removeContext(key: String)

    /**
     * Sets a tag to Scope's tags
     *
     * @param key the key
     * @param value the value
     */
    fun setTag(key: String, value: String)

    /**
     * Removes a tag from the Scope's tags
     *
     * @param key the key
     */
    fun removeTag(key: String)

    /**
     * Sets an extra to the Scope's extra map
     *
     * @param key the key
     * @param value the value
     */
    fun setExtra(key: String, value: String)

    /**
     * Removes an extra from the Scope's extras
     *
     * @param key the key
     */
    fun removeExtra(key: String)

    /** Resets the Scope to its default state */
    fun clear()
}
