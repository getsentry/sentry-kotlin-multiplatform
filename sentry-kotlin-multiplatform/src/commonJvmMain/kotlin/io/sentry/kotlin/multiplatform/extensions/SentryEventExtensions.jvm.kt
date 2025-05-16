package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryEvent
import io.sentry.kotlin.multiplatform.JvmSentryId
import io.sentry.kotlin.multiplatform.SentryEvent

// TODO(buenaflor): properly test this once we introduce a mocking framework
internal fun JvmSentryEvent.applyKmpEvent(kmpEvent: SentryEvent): JvmSentryEvent {
    // Native SDKs may have a default behaviour if no release/dist is set.
    // Setting a release/dist (even if it's null) will remove this behaviour,
    // so we have to check if the values differ first before setting.
    if (release != kmpEvent.release) {
        release = kmpEvent.release
    }
    if (dist != kmpEvent.dist) {
        dist = kmpEvent.dist
    }
    level = kmpEvent.level?.toJvmSentryLevel()
    message = kmpEvent.message?.toJvmMessage()
    logger = kmpEvent.logger
    fingerprints = kmpEvent.fingerprint
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
