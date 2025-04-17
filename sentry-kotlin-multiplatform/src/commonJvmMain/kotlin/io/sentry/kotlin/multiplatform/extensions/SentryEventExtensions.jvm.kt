package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryEvent
import io.sentry.kotlin.multiplatform.JvmSentryId
import io.sentry.kotlin.multiplatform.SentryEvent
import io.sentry.kotlin.multiplatform.util.applyIfChanged

/**
 * Syncs updated fields from a KMP [SentryEvent] to this [JvmSentryEvent].
 *
 * Only the properties modified (e.g., in beforeSend or event processors) are copied.
 *
 * @param beforeKmpEvent The KMP [SentryEvent] before the changes were applied.
 * @param afterKmpEvent The KMP [SentryEvent] after the changes were applied.
 * @return The modified [JvmSentryEvent].
 */
internal fun JvmSentryEvent.updateFromKmpEventChanges(
    beforeKmpEvent: SentryEvent,
    afterKmpEvent: SentryEvent
): JvmSentryEvent {
    applyIfChanged(beforeKmpEvent.release, afterKmpEvent.release) { release = it }
    applyIfChanged(beforeKmpEvent.dist, afterKmpEvent.dist) { dist = it }
    applyIfChanged(beforeKmpEvent.environment, afterKmpEvent.environment) { environment = it }
    applyIfChanged(beforeKmpEvent.serverName, afterKmpEvent.serverName) { serverName = it }
    applyIfChanged(beforeKmpEvent.platform, afterKmpEvent.platform) { platform = it }
    applyIfChanged(beforeKmpEvent.logger, afterKmpEvent.logger) { logger = it }
    applyIfChanged(beforeKmpEvent.level, afterKmpEvent.level) { level = it?.toJvmSentryLevel() }
    applyIfChanged(beforeKmpEvent.message, afterKmpEvent.message) { message = it?.toJvmMessage() }
    applyIfChanged(beforeKmpEvent.fingerprint, afterKmpEvent.fingerprint) { fingerprints = it }
    applyIfChanged(beforeKmpEvent.user, afterKmpEvent.user) { user = it?.toJvmUser() }
    applyIfChanged(beforeKmpEvent.breadcrumbs, afterKmpEvent.breadcrumbs) { breadcrumbs = it.map { it.toJvmBreadcrumb() } }
    applyIfChanged(beforeKmpEvent.eventId, afterKmpEvent.eventId) { eventId = JvmSentryId(it.toString()) }
    applyIfChanged(beforeKmpEvent.tags, afterKmpEvent.tags) { tags = it }
    return this
}
