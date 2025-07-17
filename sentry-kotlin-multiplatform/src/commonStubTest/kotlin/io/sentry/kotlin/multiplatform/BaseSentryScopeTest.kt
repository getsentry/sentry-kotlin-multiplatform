package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User

actual abstract class BaseSentryScopeTest {
    actual fun initializeScope(): Scope = StubScope()
}

private class StubScope(
    override var user: User? = null,
    override var level: SentryLevel? = null
) : Scope {
    override fun getTags(): MutableMap<String, String> {
        return mutableMapOf()
    }

    override fun getContexts(): MutableMap<String, Any> {
        return mutableMapOf()
    }

    override fun addAttachment(attachment: Attachment) {
    }

    override fun clearAttachments() {
    }

    override fun addBreadcrumb(breadcrumb: Breadcrumb) {
    }

    override fun clearBreadcrumbs() {
    }

    override fun setContext(key: String, value: Any) {
    }

    override fun setContext(key: String, value: Boolean) {
    }

    override fun setContext(key: String, value: String) {
    }

    override fun setContext(key: String, value: Number) {
    }

    override fun setContext(key: String, value: Collection<*>) {
    }

    override fun setContext(key: String, value: Array<*>) {
    }

    override fun setContext(key: String, value: Char) {
    }

    override fun removeContext(key: String) {
    }

    override fun setTag(key: String, value: String) {
    }

    override fun removeTag(key: String) {
    }

    override fun setExtra(key: String, value: String) {
    }

    override fun removeExtra(key: String) {
    }

    override fun clear() {
    }

}