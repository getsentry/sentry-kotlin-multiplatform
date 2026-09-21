package io.sentry.kotlin.multiplatform.metrics

import io.sentry.kotlin.multiplatform.SentryAttributes

/** A captured metric passed to [SentryMetricOptions.beforeSend]. */
@Suppress("LongParameterList")
public class SentryMetric(
    /** Unix timestamp in seconds when the metric was captured. Native-owned on write-back. */
    public val timestamp: Double,
    /** Name of the metric. */
    public var name: String,
    /** Operation and numeric value. Invalid values returned by a callback drop the metric. */
    public var value: SentryMetricValue,
    /** Optional measurement unit, for example "millisecond" or "byte". */
    public var unit: String? = null,
    /** Scalar attributes; native attributes with unsupported types are preserved. */
    public val attributes: SentryAttributes = SentryAttributes.empty(),
    /** Native trace identifier, preserved when a callback returns a replacement metric. */
    public val traceId: String,
    /** Native span identifier, if present; preserved on write-back. */
    public val spanId: String? = null,
)
