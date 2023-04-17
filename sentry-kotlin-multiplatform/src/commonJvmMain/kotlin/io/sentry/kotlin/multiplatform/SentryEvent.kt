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
        this.eventId = SentryId(jvmSentryEvent.eventId.toString())
        this.level = jvmSentryEvent.level?.toKmpSentryLevel()
        this.message = jvmSentryEvent.message?.toKmpMessage()
        this.logger = jvmSentryEvent.logger
        this.fingerprint = jvmSentryEvent.fingerprints?.toList()
        this.exceptions = jvmSentryEvent.exceptions?.map { it.toKmpSentryException() }?.toList()
        this.release = jvmSentryEvent.release
        this.environment = jvmSentryEvent.environment
        this.platform = jvmSentryEvent.platform
        this.user = jvmSentryEvent.user?.toKmpUser()
        this.serverName = jvmSentryEvent.serverName
        this.dist = jvmSentryEvent.dist
        this.mutableContexts = jvmSentryEvent.contexts
        this.mutableBreadcrumbs = jvmSentryEvent.breadcrumbs?.map { it.toKmpBreadcrumb() }?.toMutableList()
        this.mutableTags = jvmSentryEvent.tags
    }
}
