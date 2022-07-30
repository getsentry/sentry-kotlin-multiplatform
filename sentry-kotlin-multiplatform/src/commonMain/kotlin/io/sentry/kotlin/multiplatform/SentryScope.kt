package io.sentry.kotlin.multiplatform

expect class SentryScope {
    fun addBreadcrumb(crumb: SentryBreadcrumb)
    fun clearBreadcrumbs()
    fun setUser(user: SentryUser)
    fun setContext(key: String, value: Any)
    fun removeContext(key: String)
    fun setTag(key: String, value: String)
    fun removeTag(key: String)
    fun setExtra(key: String, value: String)
    fun removeExtra(key: String)
    fun setLevel(level: SentryLevel)
    fun clear()
}
