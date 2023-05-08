package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User

/**
 * Scope decorator that holds the Scope's data.
 *
 * This decorator allows to modify the native Scope directly without having to create a new object.
 *
 * Different platforms have specific providers.
 *  - For JVM: use [io.sentry.kotlin.multiplatform.JvmScopeProvider]
 *  - For Cocoa: use [io.sentry.kotlin.multiplatform.CocoaScopeProvider]
 *
 * Usage example in JVM:
 * ```
 * val jvmScopeProvider = JvmScopeProvider(nativeJvmScope)
 * val scope: Scope = ScopeDecorator(jvmScopeProvider)
 * ```
 */
internal class ScopeDecorator(
    /** [Scope] that holds the Scope's data */
    private val scopeProvider: Scope
) : Scope by scopeProvider

/** ScopeProvider that holds the Scope's data */
public interface Scope {

    /**
     * Returns the scope's tags
     */
    public fun getTags(): MutableMap<String, String>

    /**
     * Returns the Scope's contexts
     */
    public fun getContexts(): MutableMap<String, Any>

    /**
     * The Scope's user
     */
    public var user: User?

    /**
     * The Scope's level
     */
    public var level: SentryLevel?

    /**
     * Adds an attachment to the Scope's list of attachments. The SDK adds the attachment to every
     * event and transaction sent to Sentry.
     *
     * @param attachment The attachment to add to the Scope's list of attachments.
     */
    public fun addAttachment(attachment: Attachment)

    /** Clear all attachments */
    public fun clearAttachments()

    /**
     * Adds a breadcrumb to the breadcrumbs queue
     *
     * @param breadcrumb the breadcrumb
     */
    public fun addBreadcrumb(breadcrumb: Breadcrumb)

    /** Clear all the breadcrumbs */
    public fun clearBreadcrumbs()

    /**
     * Sets the Scope's contexts
     *
     * @param key the context key
     * @param value the context value
     */
    public fun setContext(key: String, value: Any)

    /**
     * Sets the Scope's contexts
     *
     * @param key the context key
     * @param value the context value
     */
    public fun setContext(key: String, value: Boolean)

    /**
     * Sets the Scope's context
     *
     * @param key the context key
     * @param value the context value
     */
    public fun setContext(key: String, value: String)

    /**
     * Sets the Scope's context
     *
     * @param key the context key
     * @param value the context value
     */
    public fun setContext(key: String, value: Number)

    /**
     * Sets the Scope's context
     *
     * @param key the context key
     * @param value the context value
     */
    public fun setContext(key: String, value: Collection<*>)

    /**
     * Sets the Scope's context
     *
     * @param key the context key
     * @param value the context value
     */
    public fun setContext(key: String, value: Array<*>)

    /**
     * Sets the Scope's context
     *
     * @param key the context key
     * @param value the context value
     */
    public fun setContext(key: String, value: Char)

    /**
     * Removes a value from the Scope's contexts
     *
     * @param key the Key
     */
    public fun removeContext(key: String)

    /**
     * Sets a tag to Scope's tags
     *
     * @param key the key
     * @param value the value
     */
    public fun setTag(key: String, value: String)

    /**
     * Removes a tag from the Scope's tags
     *
     * @param key the key
     */
    public fun removeTag(key: String)

    /**
     * Sets an extra to the Scope's extra map
     *
     * @param key the key
     * @param value the value
     */
    public fun setExtra(key: String, value: String)

    /**
     * Removes an extra from the Scope's extras
     *
     * @param key the key
     */
    public fun removeExtra(key: String)

    /** Resets the Scope to its default state */
    public fun clear()
}
