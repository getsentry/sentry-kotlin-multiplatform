package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toJvmBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toJvmSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toJvmUser
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpUser
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User

internal class JvmScopeProvider(private val scope: JvmScope) : Scope {

    override var level: SentryLevel?
        set(value) {
            scope.level = value?.toJvmSentryLevel()
        }
        get() = scope.level?.toKmpSentryLevel()

    override var user: User?
        set(value) {
            scope.user = value?.toJvmUser()
        }
        get() = scope.user?.toKmpUser()

    override fun addAttachment(attachment: Attachment) {
        scope.addAttachment(attachment.jvmAttachment)
    }

    override fun clearAttachments() {
        scope.clearAttachments()
    }

    override fun getContexts(): MutableMap<String, Any> {
        return scope.contexts
    }

    override fun getTags(): MutableMap<String, String> {
        return scope.tags
    }

    override fun addBreadcrumb(breadcrumb: Breadcrumb) {
        scope.addBreadcrumb(breadcrumb.toJvmBreadcrumb())
    }

    override fun clearBreadcrumbs() {
        scope.clearBreadcrumbs()
    }

    override fun setContext(key: String, value: Any) {
        scope.setContexts(key, value)
    }

    override fun setContext(key: String, value: Boolean) {
        scope.setContexts(key, value)
    }

    override fun setContext(key: String, value: String) {
        scope.setContexts(key, value)
    }

    override fun setContext(key: String, value: Number) {
        scope.setContexts(key, value)
    }

    override fun setContext(key: String, value: Collection<*>) {
        scope.setContexts(key, value)
    }

    override fun setContext(key: String, value: Array<*>) {
        scope.setContexts(key, value as Array<Any>)
    }

    override fun setContext(key: String, value: Char) {
        scope.setContexts(key, value)
    }

    override fun removeContext(key: String) {
        scope.removeContexts(key)
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
