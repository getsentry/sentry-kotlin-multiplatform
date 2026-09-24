package io.sentry.kotlin.multiplatform.metrics

/** A metric's operation and numeric value. */
public sealed class SentryMetricValue {
    /**
     * A finite, non-negative native counter value. Java counters may be fractional.
     * Apple requires whole numbers representable as a native unsigned integer. Large Apple
     * counters can be rounded in this view; returning the unchanged value preserves the native integer.
     */
    public data class Counter(
        /** The amount to increment by. */
        public val value: Double,
    ) : SentryMetricValue()

    /** A finite measurement of the current state. */
    public data class Gauge(
        /** The measured value. */
        public val value: Double,
    ) : SentryMetricValue()

    /** A finite sample in a distribution. */
    public data class Distribution(
        /** The measured value. */
        public val value: Double,
    ) : SentryMetricValue()
}

internal const val MAX_EXACT_COUNTER: Long = 9_007_199_254_740_992

internal fun SentryMetricValue.isValid(): Boolean =
    when (this) {
        is SentryMetricValue.Counter -> value.isFinite() && value >= 0.0
        is SentryMetricValue.Gauge -> value.isFinite()
        is SentryMetricValue.Distribution -> value.isFinite()
    }
