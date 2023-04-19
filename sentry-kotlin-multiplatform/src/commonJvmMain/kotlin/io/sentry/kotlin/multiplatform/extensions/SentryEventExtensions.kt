package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryEvent
import io.sentry.kotlin.multiplatform.JvmSentryId
import io.sentry.kotlin.multiplatform.SentryEvent

internal fun JvmSentryEvent.applyKmpEvent(kmpEvent: SentryEvent): JvmSentryEvent {
    level = kmpEvent.level?.toJvmSentryLevel()
    message = kmpEvent.message?.toJvmMessage()
    logger = kmpEvent.logger
    fingerprints = kmpEvent.fingerprint
    release = kmpEvent.release
    environment = kmpEvent.environment
    platform = kmpEvent.platform
    user = kmpEvent.user?.toJvmUser()
    serverName = kmpEvent.serverName
    dist = kmpEvent.dist
    breadcrumbs = kmpEvent.breadcrumbs?.map { it.toJvmBreadcrumb() }
    eventId = JvmSentryId(kmpEvent.eventId.toString())
    tags = kmpEvent.tags
    return this
}
