package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.protocol.Message

public actual class SentryEvent(cocoaSentryEvent: CocoaSentryEvent) : SentryBaseEvent() {
    public actual var level: SentryLevel? = cocoaSentryEvent.level?.toKmpSentryLevel()
    public actual var message: Message?
        get() = TODO("Not yet implemented")
        set(value) {}
    public actual var logger: String?
        get() = TODO("Not yet implemented")
        set(value) {}
    public actual var fingerprint: List<String>?
        get() = TODO("Not yet implemented")
        set(value) {}

    public actual fun isCrashed(): Boolean {
        TODO("Not yet implemented")
    }

    public actual fun isError(): Boolean {
        TODO("Not yet implemented")
    }
}
