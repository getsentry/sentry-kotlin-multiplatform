package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryException

/** Represents an event that is sent to Sentry. */
public expect class SentryEvent() : SentryBaseEvent {
    public var message: Message?
    public var logger: String?
    public var level: SentryLevel?

    /** This is not thread-safe */
    public var fingerprint: List<String>?

    /** This is not thread-safe */
    public var exceptions: List<SentryException>?
}
