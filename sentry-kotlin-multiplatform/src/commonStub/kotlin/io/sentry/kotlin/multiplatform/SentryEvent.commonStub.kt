package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryException

public actual class SentryEvent actual constructor() : SentryBaseEvent() {
    public actual var message: Message? = null
    public actual var logger: String? = null
    public actual var level: SentryLevel? = null
    public actual var fingerprint: MutableList<String> = mutableListOf()
    public actual var exceptions: MutableList<SentryException> = mutableListOf()
}
