package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryUser

expect class SentryScope() : ISentryScope {

    override var level: SentryLevel?
    override var user: SentryUser?
    override fun getContexts(): MutableMap<String, Any>?
    override fun getTags(): MutableMap<String, String>?
    override fun addBreadcrumb(breadcrumb: Breadcrumb)
    override fun clearBreadcrumbs()
    override fun setContext(key: String, value: Any)
    override fun setContext(key: String, value: Boolean)
    override fun setContext(key: String, value: String)
    override fun setContext(key: String, value: Number)
    override fun setContext(key: String, value: Char)
    override fun setContext(key: String, value: Array<Any>)
    override fun setContext(key: String, value: Collection<*>)
    override fun removeContext(key: String)
    override fun setTag(key: String, value: String)
    override fun removeTag(key: String)
    override fun setExtra(key: String, value: String)
    override fun removeExtra(key: String)
    override fun clear()
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
    var user: SentryUser?

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
    fun setContext(key: String, value: Array<Any>)

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
