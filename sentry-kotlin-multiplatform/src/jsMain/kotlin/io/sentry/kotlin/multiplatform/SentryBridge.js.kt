package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryException
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User
import io.sentry.kotlin.multiplatform.protocol.UserFeedback

internal actual class SentryBridge actual constructor(
    private val sentryInstance: SentryInstance = SentryPlatformInstance()
) {
    private var enabled = false
    private var options: SentryOptions? = null
    private val globalScope = InMemoryScope()

    actual fun init(context: Context, configuration: OptionsConfiguration) {
        init(configuration)
    }

    actual fun init(configuration: OptionsConfiguration) {
        options = SentryOptions().also(configuration)
        enabled = true
    }

    actual fun initWithPlatformOptions(configuration: PlatformOptionsConfiguration) {
        sentryInstance.init(configuration)
        enabled = true
    }

    // region Capture APIs
    actual fun captureMessage(message: String): SentryId = captureMessage(message) {}

    actual fun captureMessage(message: String, scopeCallback: ScopeCallback): SentryId {
        val localScope = InMemoryScope().also(scopeCallback)
        val event = SentryEvent().apply { this.message = Message(formatted = message) }
        applyScopes(event, localScope)
        return dispatch(event)
    }

    actual fun captureException(throwable: Throwable): SentryId = captureException(throwable) {}

    actual fun captureException(
        throwable: Throwable,
        scopeCallback: ScopeCallback
    ): SentryId {
        val localScope = InMemoryScope().also(scopeCallback)
        val event = SentryEvent().apply {
            exceptions = mutableListOf(
                SentryException(
                    type = throwable::class.simpleName,
                    value = throwable.message
                )
            )
        }
        applyScopes(event, localScope)
        return dispatch(event)
    }
    // endregion

    // region Scope helpers
    actual fun configureScope(scopeCallback: ScopeCallback) = scopeCallback(globalScope)

    actual fun captureUserFeedback(userFeedback: UserFeedback) { /* no-op */ }

    actual fun addBreadcrumb(breadcrumb: Breadcrumb) = globalScope.addBreadcrumb(breadcrumb)

    actual fun setUser(user: User?) { globalScope.user = user }

    actual fun isCrashedLastRun(): Boolean = false

    actual fun isEnabled(): Boolean = enabled

    actual fun close() {
        enabled = false
    }
    // endregion

    private fun applyScopes(event: SentryEvent, local: InMemoryScope) {
        event.tags.putAll(globalScope.getTags())
        event.tags.putAll(local.getTags())
        event.level = local.level ?: globalScope.level
        event.user = local.user ?: globalScope.user
        event.contexts = globalScope.getContexts() + local.getContexts()
    }

    private fun dispatch(event: SentryEvent): SentryId {
        val id = SentryId(generateRandomId())
        event.eventId = id
        options?.beforeSend?.invoke(event)
        return id
    }

    private fun generateRandomId(): String =
        kotlin.random.Random.nextBytes(16).joinToString("") { "%02x".format(it) }
}

/* Simple in-memory Scope implementation shared between bridge and tests */
internal class InMemoryScope : Scope {
    private val _tags = mutableMapOf<String, String>()
    private val _contexts = mutableMapOf<String, Any>()
    private val _breadcrumbs = mutableListOf<Breadcrumb>()
    private val _attachments = mutableListOf<Attachment>()

    override var user: User? = null
    override var level: SentryLevel? = null

    override fun getTags(): MutableMap<String, String> = _tags
    override fun getContexts(): MutableMap<String, Any> = _contexts

    override fun addAttachment(attachment: Attachment) { _attachments.add(attachment) }
    override fun clearAttachments() { _attachments.clear() }

    override fun addBreadcrumb(breadcrumb: Breadcrumb) { _breadcrumbs.add(breadcrumb) }
    override fun clearBreadcrumbs() { _breadcrumbs.clear() }

    private fun wrap(value: Any): Map<String, Any> = mapOf("value" to value)

    override fun setContext(key: String, value: Any) { _contexts[key] = value }
    override fun setContext(key: String, value: Boolean) { _contexts[key] = wrap(value) }
    override fun setContext(key: String, value: String) { _contexts[key] = wrap(value) }
    override fun setContext(key: String, value: Number) { _contexts[key] = wrap(value) }
    override fun setContext(key: String, value: Collection<*>) { _contexts[key] = wrap(value) }
    override fun setContext(key: String, value: Array<*>) { _contexts[key] = wrap(value) }
    override fun setContext(key: String, value: Char) { _contexts[key] = wrap(value) }
    override fun removeContext(key: String) { _contexts.remove(key) }

    override fun setTag(key: String, value: String) { _tags[key] = value }
    override fun removeTag(key: String) { _tags.remove(key) }

    override fun setExtra(key: String, value: String) { _contexts[key] = value }
    override fun removeExtra(key: String) { _contexts.remove(key) }

    override fun clear() {
        _tags.clear()
        _contexts.clear()
        _breadcrumbs.clear()
        _attachments.clear()
        user = null
        level = null
    }
}