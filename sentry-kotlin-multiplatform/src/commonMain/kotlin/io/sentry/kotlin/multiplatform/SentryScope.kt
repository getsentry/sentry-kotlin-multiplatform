package io.sentry.kotlin.multiplatform

expect class SentryScope() : ISentryScope {

    override fun addBreadcrumb(breadcrumb: SentryBreadcrumb)
    override fun clearBreadcrumbs()
    override fun setUser(user: SentryUser)
    override fun getUser(): SentryUser?
    override fun setContext(key: String, value: Any)
    override fun removeContext(key: String)
    override fun getContext(): Map<String, Any>?
    override fun setTag(key: String, value: String)
    override fun removeTag(key: String)
    override fun setExtra(key: String, value: String)
    override fun removeExtra(key: String)
    override fun setLevel(level: SentryLevel)
    override fun getLevel(): SentryLevel?
    override fun clear()
}

interface ISentryScope {

    /**
     * Adds a breadcrumb to the breadcrumbs queue
     *
     * @param breadcrumb the breadcrumb
     */
    fun addBreadcrumb(breadcrumb: SentryBreadcrumb)

    /** Clear all the breadcrumbs */
    fun clearBreadcrumbs()

    /**
     * Sets the Scope's user
     *
     * @param user the user
     */
    fun setUser(user: SentryUser)

    /**
     * Returns the Scope's user
     */
    fun getUser(): SentryUser?

    /**
     * Sets the Scope's contexts
     *
     * @param key the context key
     * @param value the context value
     */
    fun setContext(key: String, value: Any)

    /**
     * Returns the Scope's context
     */
    fun getContext(): Map<String, Any>?

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

    /**
     * Sets the Scope's SentryLevel Level from scope exceptionally take precedence over the event
     *
     * @param level the SentryLevel
     */
    fun setLevel(level: SentryLevel)

    /**
     * Returns the Scope's level
     */
    fun getLevel(): SentryLevel?

    /** Resets the Scope to its default state */
    fun clear()
}
