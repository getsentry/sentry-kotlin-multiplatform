package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId

/**
 * An interface representing the context of a span.
 *
 * @property operation The name of the operation associated with the span.
 * @property traceId Determines which trace the span belongs to.
 * @property spanId The unique identifier of the span.
 * @property parentSpanId The unique identifier of the span's parent, if any.
 * @property description A longer description of the span's operation, which uniquely identifies the span but is
 * consistent across instances of the span.
 * @property sampled Indicates if the span is sampled.
 */
public interface SpanContext {
    public val operation: String
    public val traceId: SentryId
    public val spanId: SpanId
    public val parentSpanId: SpanId?
    public val description: String?
    public val sampled: Boolean?
}
