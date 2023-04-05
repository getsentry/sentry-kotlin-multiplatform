package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryException
import io.sentry.kotlin.multiplatform.protocol.SentryException

internal fun JvmSentryException.toKmpSentryException() = SentryException(
    type = this.type,
    value = this.value,
    module = this.module,
    threadId = this.threadId
)