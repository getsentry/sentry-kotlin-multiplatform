package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId

public expect class SentryTraceHeader {
  public fun getTraceId(): SentryId

  public fun getSpanId(): SpanId

  public fun isSampled(): Boolean?
}
