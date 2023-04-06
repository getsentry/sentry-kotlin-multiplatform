package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryException

/** Represents an event that is sent to Sentry. */
public expect class SentryEvent() : SentryBaseEvent {
    public var message: Message?
    public var logger: String?
    public var level: SentryLevel?
    public var fingerprint: List<String>?
    public var exceptions: List<SentryException>?
}
