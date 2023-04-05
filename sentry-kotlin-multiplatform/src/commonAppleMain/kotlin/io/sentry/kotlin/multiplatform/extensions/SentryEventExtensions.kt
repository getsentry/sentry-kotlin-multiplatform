package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryId
import io.sentry.kotlin.multiplatform.CocoaSentryEvent
import io.sentry.kotlin.multiplatform.SentryEvent

internal fun CocoaSentryEvent.applyKmpEvent(kmpEvent: SentryEvent): CocoaSentryEvent {
    kmpEvent.level?.let { this.level = it.toCocoaSentryLevel() }
    kmpEvent.platform?.let { this.platform = it }
    this.message = kmpEvent.message?.toCocoaMessage()
    this.logger = kmpEvent.logger
    this.fingerprint = kmpEvent.fingerprint
    this.releaseName = kmpEvent.release
    this.environment = kmpEvent.environment
    this.user = kmpEvent.user?.toCocoaUser()
    this.serverName = kmpEvent.serverName
    this.dist = kmpEvent.dist
    this.breadcrumbs = kmpEvent.breadcrumbs?.map { it.toCocoaBreadcrumb() }?.toMutableList()
    this.tags = kmpEvent.tags?.toMutableMap()
    this.eventId = SentryId(kmpEvent.eventId.toString())
    return this
}