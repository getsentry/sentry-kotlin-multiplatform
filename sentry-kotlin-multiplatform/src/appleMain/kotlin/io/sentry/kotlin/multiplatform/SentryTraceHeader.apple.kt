package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toBoolean
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId

public actual class SentryTraceHeader
constructor(private val cocoaSentryTraceHeader: CocoaSentryTraceHeader) {
  public actual fun getTraceId(): SentryId {
    return SentryId(cocoaSentryTraceHeader.traceId.sentryIdString)
  }

  public actual fun getSpanId(): SpanId {
    return SpanId(cocoaSentryTraceHeader.spanId.sentrySpanIdString)
  }

  public actual fun isSampled(): Boolean? {
    return cocoaSentryTraceHeader.sampled.toBoolean()
  }
}
