package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toCocoaBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toCocoaSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toCocoaUser
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpUser
import io.sentry.kotlin.multiplatform.extensions.toMutableMap
import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.User
import Scope.Sentry.SentryScope as PrivateCocoaScope

internal class CocoaScopeProvider(private val scope: CocoaScope) : Scope {

    /*
     This bridge exposes private Cocoa SDK API to fetch internal properties such as user, level, etc.
     We need this in order to return properties because the Cocoa SDK doesn't implement getters.
     This is only used for get methods.
     */
    private val privateScope = scope as? PrivateCocoaScope

    override var level: SentryLevel?
        set(value) {
            value?.let { scope.setLevel(it.toCocoaSentryLevel()) }
        }
        get() {
            return privateScope?.levelEnum?.toKmpSentryLevel()
        }

    override var user: User?
        set(value) {
            scope.setUser(value?.toCocoaUser())
        }
        get() {
            val privateUser = privateScope?.userObject as? CocoaUser
            return privateUser?.let { it.toKmpUser() }
        }

    override fun addAttachment(attachment: Attachment) {
        scope.addAttachment(attachment.cocoaAttachment)
    }

    override fun clearAttachments() {
        scope.clearAttachments()
    }

    override fun getContexts(): MutableMap<String, Any> {
        val map = privateScope?.contextDictionary?.toMutableMap<String, Any>()
        map?.let { return it }
        return HashMap()
    }

    override fun getTags(): MutableMap<String, String> {
        val map = privateScope?.tagDictionary?.toMutableMap<String, String>()
        map?.let { return it }
        return HashMap()
    }

    override fun addBreadcrumb(breadcrumb: Breadcrumb) {
        scope.addBreadcrumb(breadcrumb.toCocoaBreadcrumb())
    }

    override fun clearBreadcrumbs() {
        scope.clearBreadcrumbs()
    }

    private fun setContextForPrimitiveValues(key: String, value: Any) {
        scope.setContextValue(mapOf("value" to value), key)
    }

    override fun setContext(key: String, value: Any) {
        try {
            (value as? Map<Any?, Any>)?.let {
                scope.setContextValue(it, key)
            }
        } catch (e: Throwable) {
            setContextForPrimitiveValues(key, value)
        }
    }

    override fun setContext(key: String, value: String) {
        setContextForPrimitiveValues(key, value)
    }

    override fun setContext(key: String, value: Boolean) {
        setContextForPrimitiveValues(key, value)
    }

    override fun setContext(key: String, value: Number) {
        setContextForPrimitiveValues(key, value)
    }

    override fun setContext(key: String, value: Char) {
        setContextForPrimitiveValues(key, value)
    }

    override fun setContext(key: String, value: Array<*>) {
        setContextForPrimitiveValues(key, value)
    }

    override fun setContext(key: String, value: Collection<*>) {
        setContextForPrimitiveValues(key, value)
    }

    override fun removeContext(key: String) {
        scope.removeContextForKey(key)
    }

    override fun setTag(key: String, value: String) {
        scope.setTagValue(value, key)
    }

    override fun removeTag(key: String) {
        scope.removeTagForKey(key)
    }

    override fun setExtra(key: String, value: String) {
        scope.setExtraValue(value, key)
    }

    override fun removeExtra(key: String) {
        scope.removeExtraForKey(key)
    }

    override fun clear() {
        scope.clear()
    }
}
