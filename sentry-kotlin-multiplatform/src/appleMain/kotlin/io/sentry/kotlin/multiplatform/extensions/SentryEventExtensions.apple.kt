package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryId
import io.sentry.kotlin.multiplatform.CocoaSentryEvent
import io.sentry.kotlin.multiplatform.SentryEvent

internal fun CocoaSentryEvent.applyKmpEvent(kmpEvent: SentryEvent): CocoaSentryEvent {
    // Native SDKs may have a default behaviour if no release/dist is set.
    // Setting a release/dist (even if it's null) will remove this behaviour,
    // so we have to check if the values differ first before setting.
    if (releaseName != kmpEvent.release) {
        releaseName = kmpEvent.release
    }
    if (dist != kmpEvent.dist) {
        dist = kmpEvent.dist
    }
    kmpEvent.level?.let { level = it.toCocoaSentryLevel() }
    kmpEvent.platform?.let { platform = it }
    message = kmpEvent.message?.toCocoaMessage()
    logger = kmpEvent.logger
    fingerprint = kmpEvent.fingerprint
    environment = kmpEvent.environment
    user = kmpEvent.user?.toCocoaUser()
    serverName = kmpEvent.serverName
    dist = kmpEvent.dist
    breadcrumbs = kmpEvent.breadcrumbs.map { it.toCocoaBreadcrumb() }.toMutableList()
    tags = kmpEvent.tags.toMutableMap()
    eventId = SentryId(kmpEvent.eventId.toString())
    return this
}
