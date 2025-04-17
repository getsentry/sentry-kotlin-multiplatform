package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryId
import io.sentry.kotlin.multiplatform.CocoaSentryEvent
import io.sentry.kotlin.multiplatform.SentryEvent
import io.sentry.kotlin.multiplatform.util.applyIfChanged

/**
 * Syncs updated fields from a KMP [SentryEvent] to this [CocoaSentryEvent].
 *
 * Only the properties modified (e.g., in beforeSend or event processors) are copied.
 *
 * @param beforeKmpEvent The KMP [SentryEvent] before the changes were applied.
 * @param afterKmpEvent The KMP [SentryEvent] after the changes were applied.
 * @return The modified [CocoaSentryEvent].
 */
internal fun CocoaSentryEvent.updateFromKmpEventChanges(
    beforeKmpEvent: SentryEvent,
    afterKmpEvent: SentryEvent
): CocoaSentryEvent {
    applyIfChanged(beforeKmpEvent.release, afterKmpEvent.release) { releaseName = it }
    applyIfChanged(beforeKmpEvent.dist, afterKmpEvent.dist) { dist = it }
    applyIfChanged(beforeKmpEvent.environment, afterKmpEvent.environment) { environment = it }
    applyIfChanged(beforeKmpEvent.serverName, afterKmpEvent.serverName) { serverName = it }
    applyIfChanged(beforeKmpEvent.platform, afterKmpEvent.platform) {
        if (it != null) {
            platform = it
        }
    }
    applyIfChanged(beforeKmpEvent.logger, afterKmpEvent.logger) { logger = it }
    applyIfChanged(beforeKmpEvent.level, afterKmpEvent.level) {
        if (it != null) level = it.toCocoaSentryLevel()
    }
    applyIfChanged(beforeKmpEvent.message, afterKmpEvent.message) { message = it?.toCocoaMessage() }
    applyIfChanged(beforeKmpEvent.fingerprint, afterKmpEvent.fingerprint) { fingerprint = it }
    applyIfChanged(beforeKmpEvent.user, afterKmpEvent.user) { user = it?.toCocoaUser() }
    applyIfChanged(beforeKmpEvent.breadcrumbs, afterKmpEvent.breadcrumbs) {
        breadcrumbs = it.map { it.toCocoaBreadcrumb() }.toMutableList()
    }
    applyIfChanged(beforeKmpEvent.eventId, afterKmpEvent.eventId) {
        eventId = SentryId(it.toString())
    }
    applyIfChanged(beforeKmpEvent.tags, afterKmpEvent.tags) { tags = it.toMutableMap() }
    return this
}
