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
    public actual var fingerprint: MutableList<String> = mutableListOf()
    public actual var exceptions: MutableList<SentryException> = mutableListOf()
    public override var release: String? = null
    public override var environment: String? = null
    public override var platform: String? = null
    public override var user: User? = null
    public override var serverName: String? = null
    public override var dist: String? = null

    public constructor(cocoaSentryEvent: CocoaSentryEvent) : this() {
        eventId = SentryId(cocoaSentryEvent.eventId.toString())
        level = cocoaSentryEvent.level?.toKmpSentryLevel()
        message = cocoaSentryEvent.message?.toKmpMessage()
        logger = cocoaSentryEvent.logger
        release = cocoaSentryEvent.releaseName
        environment = cocoaSentryEvent.environment
        platform = cocoaSentryEvent.platform
        user = cocoaSentryEvent.user?.toKmpUser()
        serverName = cocoaSentryEvent.serverName
        dist = cocoaSentryEvent.dist

        val cocoaFingerprint =
            cocoaSentryEvent.fingerprint()?.toMutableList() as? MutableList<String>
        val cocoaSentryExceptions =
            cocoaSentryEvent.exceptions?.map { (it as CocoaSentryException).toKmpSentryException() }
                ?.toMutableList()
        val cocoaContexts =
            cocoaSentryEvent.context?.mapKeys { it.key as String }?.mapValues { it.value as Any }
        val cocoaBreadcrumbs = cocoaSentryEvent.breadcrumbs?.mapNotNull { it as? CocoaBreadcrumb }
            ?.map { it.toKmpBreadcrumb() }?.toMutableList()
        val cocoaTags =
            cocoaSentryEvent.tags?.mapKeys { it.key as String }?.mapValues { it.value as String }
                ?.toMutableMap()

        cocoaFingerprint?.let { fingerprint = it }
        cocoaSentryExceptions?.let { exceptions = it }
        cocoaContexts?.let { contexts = it }
        cocoaBreadcrumbs?.let { breadcrumbs = it }
        cocoaTags?.let { tags = it }
    }
}
