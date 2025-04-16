package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryEvent
import io.sentry.kotlin.multiplatform.JvmSentryId
import io.sentry.kotlin.multiplatform.SentryEvent

internal fun JvmSentryEvent.applyKmpEvent(kmpEvent: SentryEvent): JvmSentryEvent {
    kmpEvent.level?.let { level = it.toJvmSentryLevel() }
    kmpEvent.message?.let { message = it.toJvmMessage() }
    kmpEvent.logger?.let { logger = it }
    kmpEvent.release?.let { release = it }
    kmpEvent.environment?.let { environment = it }
    kmpEvent.platform?.let { platform = it }
    kmpEvent.user?.let { user = it.toJvmUser() }
    kmpEvent.serverName?.let { serverName = it }
    kmpEvent.dist?.let { dist = it }
    fingerprints = kmpEvent.fingerprint
    breadcrumbs = kmpEvent.breadcrumbs.map { it.toJvmBreadcrumb() }
    eventId = JvmSentryId(kmpEvent.eventId.toString())
    tags = kmpEvent.tags
    return this
}
