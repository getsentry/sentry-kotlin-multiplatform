package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryEvent
import io.sentry.kotlin.multiplatform.SentryEvent

internal fun JvmSentryEvent.applyKmpEvent(kmpEvent: SentryEvent): JvmSentryEvent {
    level = kmpEvent.level?.toJvmSentryLevel()
    message = kmpEvent.message?.toJvmMessage()
    logger = kmpEvent.logger
    fingerprints = kmpEvent.fingerprint
    return this
}
