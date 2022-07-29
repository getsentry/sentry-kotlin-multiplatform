package io.sentry.kotlin.multiplatform

import io.sentry.Scope
import io.sentry.protocol.User
import io.sentry.util.CollectionUtils

actual class SentryScope : ISentryScope {
    private var scope: Scope? = null

    actual override fun getUser(): SentryUser {
        return SentryUser()
    }

    actual override fun setUser(user: SentryUser) {
        val androidUser = User()
        androidUser.id = user.id
        androidUser.username = user.username
        androidUser.email = user.email
        androidUser.ipAddress = user.ipAddress
        androidUser.others = CollectionUtils.newConcurrentHashMap(user.other)
        androidUser.unknown = CollectionUtils.newConcurrentHashMap(user.unknown)
        scope?.user = androidUser
    }

    actual override fun getContexts(): SentryContext {
        return SentryContext()
    }

    actual override fun setContext(key: String, value: Any) {
        scope?.setContexts(key, value)
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

    actual override fun getLevel(): SentryLevel {
        return SentryLevel.FATAL
    }

    actual override fun setLevel(level: SentryLevel) {

    }

    actual override fun clear() {
        scope?.clear()
    }
}
