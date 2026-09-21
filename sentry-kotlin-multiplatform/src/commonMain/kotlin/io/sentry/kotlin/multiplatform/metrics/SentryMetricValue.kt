package io.sentry.kotlin.multiplatform.metrics

/** A metric's operation and numeric value. */
public sealed class SentryMetricValue {
    /** A non-negative integer increment, at most 2^53 for exact native conversion. */
    public data class Counter(
        /** The amount to increment by. */
        public val value: Long,
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
        is SentryMetricValue.Counter -> value in 0..MAX_EXACT_COUNTER
        is SentryMetricValue.Gauge -> value.isFinite()
        is SentryMetricValue.Distribution -> value.isFinite()
    }
