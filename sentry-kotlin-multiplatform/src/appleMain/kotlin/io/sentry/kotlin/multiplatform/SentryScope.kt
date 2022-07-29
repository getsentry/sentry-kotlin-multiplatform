package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import cocoapods.Sentry.SentryScope as CocoaScope
import cocoapods.Sentry.SentryUser as User

actual class SentryScope : ISentryScope {
    private var scope: CocoaScope? = null

    actual override fun getUser(): SentryUser {
        return SentryUser()
    }

    actual override fun setUser(user: SentryUser) {
        val cocoaUser = User()
        cocoaUser.userId = user.id
        cocoaUser.username = user.username
        cocoaUser.email = user.email
        cocoaUser.ipAddress = user.ipAddress
        scope?.setUser(cocoaUser)
    }

    actual override fun getContexts(): SentryContext {
        return SentryContext()
    }

    actual override fun setContext(key: String, value: Any) {
        // setting scope for ios needs map as value
    }

    actual override fun setTag(key: String, value: String) {
        scope?.setTagValue(key, value)
    }

    actual override fun removeTag(key: String) {
        scope?.removeTagForKey(key)
    }

    actual override fun setExtra(key: String, value: String) {
        // setExtra needs map
    }

    actual override fun removeExtra(key: String) {
        scope?.removeExtraForKey(key)
    }

    actual override fun getLevel(): SentryLevel {
        return SentryLevel.FATAL
    }

    actual override fun setLevel(level: SentryLevel) {
        scope?.setLevel(level.toCocoaSentryLevel())
    }

    actual override fun clear() {
        scope?.clear()
    }
}
