package io.sentry.kotlin.multiplatform

expect class SentryScope: ISentryScope {
    override fun getUser(): SentryUser
    override fun setUser(user: SentryUser)
    override fun getContexts(): SentryContext
    override fun setContext(key: String, value: Any)
    override fun setTag(key: String, value: String)
    override fun removeTag(key: String)
    override fun setExtra(key: String, value: String)
    override fun removeExtra(key: String)
    override fun getLevel(): SentryLevel
    override fun setLevel(level: SentryLevel)
    override fun clear()
}

interface ISentryScope {
    fun getUser(): SentryUser
    fun setUser(user: SentryUser)
    fun getContexts(): SentryContext
    fun setContext(key: String, value: Any) // TODO: add more setContext overload later
    fun setTag(key: String, value: String)
    fun removeTag(key: String)
    fun setExtra(key: String, value: String)
    fun removeExtra(key: String)
    fun getLevel(): SentryLevel
    fun setLevel(level: SentryLevel)
    // TODO: breadcrumbs
    fun clear()
}
