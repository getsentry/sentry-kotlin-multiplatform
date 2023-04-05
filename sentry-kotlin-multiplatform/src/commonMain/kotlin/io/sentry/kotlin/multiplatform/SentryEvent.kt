package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryException

public expect class SentryEvent() : SentryBaseEvent {
    public var message: Message?
    public var logger: String?
    public var level: SentryLevel?
    public var fingerprint: List<String>?
    public var exceptions: List<SentryException>?
}
