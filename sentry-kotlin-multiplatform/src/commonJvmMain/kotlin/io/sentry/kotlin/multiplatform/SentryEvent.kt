package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmpBreadcrumb
import io.sentry.kotlin.multiplatform.extensions.toKmpMessage
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryException
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.extensions.toKmpUser
import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryException
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.User

public actual class SentryEvent actual constructor() : SentryBaseEvent() {

    public actual var level: SentryLevel? = null
    public actual var message: Message? = null
    public actual var logger: String? = null
    public actual var fingerprint: List<String>? = null
    public actual var exceptions: List<SentryException>? = null
    public override var release: String? = null
    public override var environment: String? = null
    public override var platform: String? = null
    public override var user: User? = null
    public override var serverName: String? = null
    public override var dist: String? = null

    public constructor(jvmSentryEvent: JvmSentryEvent) : this() {
        eventId = SentryId(jvmSentryEvent.eventId.toString())
        level = jvmSentryEvent.level?.toKmpSentryLevel()
        message = jvmSentryEvent.message?.toKmpMessage()
        logger = jvmSentryEvent.logger
        fingerprint = jvmSentryEvent.fingerprints?.toList()
        exceptions = jvmSentryEvent.exceptions?.map { it.toKmpSentryException() }?.toList()
        release = jvmSentryEvent.release
        environment = jvmSentryEvent.environment
        platform = jvmSentryEvent.platform
        user = jvmSentryEvent.user?.toKmpUser()
        serverName = jvmSentryEvent.serverName
        dist = jvmSentryEvent.dist
        _contexts = jvmSentryEvent.contexts
        jvmSentryEvent.breadcrumbs?.forEach { breadcrumb ->
            addBreadcrumb(breadcrumb.toKmpBreadcrumb())
        }
        jvmSentryEvent.tags?.forEach { (key, value) ->
            setTag(key, value)
        }
    }
}
