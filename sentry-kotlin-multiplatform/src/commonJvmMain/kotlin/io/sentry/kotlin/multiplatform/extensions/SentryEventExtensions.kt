package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryEvent
import io.sentry.kotlin.multiplatform.SentryEvent

// Todo: test how null values are behaving
internal fun JvmSentryEvent.applyKmpEvent(kmpEvent: SentryEvent): JvmSentryEvent {
    this.level = kmpEvent.level?.toJvmSentryLevel()
    this.message = kmpEvent.message?.toJvmMessage()
    this.logger = kmpEvent.logger
    this.fingerprints = kmpEvent.fingerprint
    this.release = kmpEvent.release
    this.environment = kmpEvent.environment
    this.platform = kmpEvent.platform
    this.user = kmpEvent.user?.toJvmUser()
    this.serverName = kmpEvent.serverName
    this.dist = kmpEvent.dist
    this.breadcrumbs = kmpEvent.getBreadcrumbs()?.map { it.toJvmBreadcrumb() }?.toMutableList()
    this.tags = kmpEvent.getTags()?.toMutableMap()
    return this
}
