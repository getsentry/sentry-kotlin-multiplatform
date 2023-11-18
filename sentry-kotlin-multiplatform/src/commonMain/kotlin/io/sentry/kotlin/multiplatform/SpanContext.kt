package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId

/** An interface representing the context of a span. */
public interface SpanContext {
  /** The name of the operation associated with the span. */
  public val operation: String

  /** Determines which trace the span belongs to. */
  public val traceId: SentryId

  /** The unique identifier of the span. */
  public val spanId: SpanId

  /** The unique identifier of the span's parent, if any. */
  public val parentSpanId: SpanId?

  /**
   * A longer description of the span's operation, which uniquely identifies the span but is
   * consistent across instances of the span.
   */
  public val description: String?

  /** Indicates if the span is sampled. */
  public val sampled: Boolean
}
