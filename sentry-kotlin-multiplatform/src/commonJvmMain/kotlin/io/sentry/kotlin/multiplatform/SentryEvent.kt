package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toKmpMessage
import io.sentry.kotlin.multiplatform.extensions.toKmpSentryLevel
import io.sentry.kotlin.multiplatform.protocol.Message

public actual class SentryEvent(jvmSentryEvent: JvmSentryEvent) : SentryBaseEvent() {
    public actual var level: SentryLevel? = jvmSentryEvent.level?.toKmpSentryLevel()
    public actual var message: Message? = jvmSentryEvent.message?.toKmpMessage()
    public actual var logger: String? = jvmSentryEvent.logger
    public actual var fingerprint: List<String>? = jvmSentryEvent.fingerprints?.toList()

    public actual fun isCrashed(): Boolean {
        TODO("Not yet implemented")
    }

    public actual fun isError(): Boolean {
        TODO("Not yet implemented")
    }
}
