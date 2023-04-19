package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Breadcrumb
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User

/** Base class for all Sentry events. */
public abstract class SentryBaseEvent(public open var eventId: SentryId = SentryId.EMPTY_ID) {

    public open var release: String? = null
    public open var environment: String? = null
    public open var platform: String? = null
    public open var user: User? = null
    public open var serverName: String? = null
    public open var dist: String? = null

    /** This is not thread-safe */
    public var contexts: Map<String, Any>? = mapOf()
        internal set

    /** This is not thread-safe */
    public open var breadcrumbs: MutableList<Breadcrumb>? = mutableListOf()

    /** This is not thread-safe */
    public open var tags: MutableMap<String, String>? = mutableMapOf()
}
