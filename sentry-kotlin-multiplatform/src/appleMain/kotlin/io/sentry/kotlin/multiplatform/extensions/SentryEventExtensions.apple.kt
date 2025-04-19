package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryId
import io.sentry.kotlin.multiplatform.CocoaSentryEvent
import io.sentry.kotlin.multiplatform.SentryEvent

internal fun CocoaSentryEvent.applyKmpEvent(kmpEvent: SentryEvent): CocoaSentryEvent {
    kmpEvent.level?.let { level = it.toCocoaSentryLevel() }
    kmpEvent.platform?.let { platform = it }
    kmpEvent.release?.let { releaseName = it }
    kmpEvent.message?.let { message = it.toCocoaMessage() }
    kmpEvent.logger?.let { logger = it }
    kmpEvent.environment?.let { environment = it }
    kmpEvent.user?.let { user = it.toCocoaUser() }
    kmpEvent.serverName?.let { serverName = it }
    kmpEvent.dist?.let { dist = it }
    fingerprint = kmpEvent.fingerprint
    breadcrumbs = kmpEvent.breadcrumbs.map { it.toCocoaBreadcrumb() }.toMutableList()
    tags = kmpEvent.tags.toMutableMap()
    eventId = SentryId(kmpEvent.eventId.toString())
    return this
}
