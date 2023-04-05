package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.CocoaSentryException
import io.sentry.kotlin.multiplatform.protocol.SentryException

internal fun CocoaSentryException.toKmpSentryException() = SentryException(
    type = this.type,
    value = this.value,
    module = this.module,
    threadId = this.threadId as Long?
)