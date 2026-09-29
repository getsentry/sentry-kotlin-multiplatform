package io.sentry.kotlin.multiplatform.metrics

import io.sentry.kotlin.multiplatform.SentryAttributes

/**
 * Represents a captured metric.
 *
 * This is used to be able to modify metrics within [SentryMetricOptions.beforeSend].
 */
@Suppress("LongParameterList")
public class SentryMetric internal constructor(
    /** Unix timestamp in seconds when the metric was captured. */
    public val timestamp: Double,
    /** The type of this metric. */
    public val type: SentryMetricType,
    /** The metric name. */
    public var name: String,
    /** The metric value. Counters must be non-negative, and whole numbers on Apple platforms. */
    public var value: Double,
    /** Optional measurement unit, for example "millisecond" or "byte". */
    public var unit: String?,
    /** Custom key-value attributes attached to this metric. */
    public val attributes: SentryAttributes,
    /** The ID of the trace this metric belongs to. */
    public val traceId: String,
    /** The ID of the span this metric belongs to, if any. */
    public val spanId: String?,
)

internal fun SentryMetric.isValid(): Boolean = value.isFinite() && (type != SentryMetricType.COUNTER || value >= 0)
