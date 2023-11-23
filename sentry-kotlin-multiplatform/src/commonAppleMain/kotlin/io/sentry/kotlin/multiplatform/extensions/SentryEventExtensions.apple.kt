package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryId
import io.sentry.kotlin.multiplatform.CocoaSentryEvent
import io.sentry.kotlin.multiplatform.SentryEvent

internal fun CocoaSentryEvent.applyKmpEvent(kmpEvent: SentryEvent): CocoaSentryEvent {
    kmpEvent.level?.let { level = it.toCocoaSentryLevel() }
    kmpEvent.platform?.let { platform = it }
    message = kmpEvent.message?.toCocoaMessage()
    logger = kmpEvent.logger
    fingerprint = kmpEvent.fingerprint
    releaseName = kmpEvent.release
    environment = kmpEvent.environment
    user = kmpEvent.user?.toCocoaUser()
    serverName = kmpEvent.serverName
    dist = kmpEvent.dist
    breadcrumbs = kmpEvent.breadcrumbs.map { it.toCocoaBreadcrumb() }.toMutableList()
    tags = kmpEvent.tags.toMutableMap()
    eventId = SentryId(kmpEvent.eventId.toString())
    return this
}
