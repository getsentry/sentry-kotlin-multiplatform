package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Message

public expect class SentryEvent : SentryBaseEvent {
    public var message: Message?
    public var logger: String?
    public var level: SentryLevel?
    public var fingerprint: List<String>?
    public fun isCrashed(): Boolean
    public fun isError(): Boolean
}
