package io.sentry.kotlin.multiplatform.metrics

/**
 * The type of a [SentryMetric].
 */
public enum class SentryMetricType {
    /** A value that is incremented, such as the number of requests. */
    COUNTER,

    /** A value that represents the current state, such as the queue size. */
    GAUGE,

    /** A sampled value whose distribution is tracked, such as request durations. */
    DISTRIBUTION,
}
