package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User

/** Base class for all Sentry events. */
public abstract class SentryBaseEvent(
    /** The Sentry event ID. */
    public open var eventId: SentryId = SentryId.EMPTY_ID
) {
    /** The event release. */
    public open var release: String? = null

    /**
     * The event environment.
     *
     * This string is freeform and not set by default. A release can be associated with more than
     * one environment to separate them in the UI Think staging vs prod or similar.
     */
    public open var environment: String? = null

    /**
     * The event platform identifier.
     *
     * A string representing the platform the SDK is submitting from. This will be used by the
     * Sentry interface to customize various components in the interface, but also to enter or skip
     * stacktrace processing.
     */
    public open var platform: String? = null

    /** Information about the user who triggered this event. */
    public open var user: User? = null

    /** Server or device name the event was generated on. */
    public open var serverName: String? = null

    /** The event distribution. Think about it together with release and environment */
    public open var dist: String? = null

    /**
     * The event contexts describing the environment (e.g. device, os or browser).
     *
     * This is not thread-safe.
     */
    public var contexts: Map<String, Any> = mapOf()
        internal set

    /**
     * A mutable map of breadcrumbs that led to this event.
     *
     * This is not thread-safe.
     */
    public open var breadcrumbs: MutableList<Breadcrumb> = mutableListOf()

    /**
     * A mutable map of custom tags for this event where each tag must be less than 200 characters.
     *
     * This is not thread-safe.
     */
    public open var tags: MutableMap<String, String> = mutableMapOf()

    public fun getTag(key: String): String? = tags.get(key)

    public fun removeTag(key: String) {
        tags.remove(key)
    }

    public fun setTag(key: String, value: String) {
        tags.set(key, value)
    }

    public fun addBreadcrumb(breadcrumb: Breadcrumb) {
        breadcrumbs.add(breadcrumb)
    }

    public fun addBreadcrumb(message: String?) {
        addBreadcrumb(Breadcrumb(message = message))
    }
}
