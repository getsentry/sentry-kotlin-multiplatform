package io.sentry.kotlin.multiplatform.metrics

import io.sentry.kotlin.multiplatform.SentryAttributes

/**
 * The Sentry metrics API for recording counters, gauges and distributions.
 *
 * Metrics are connected to the active trace. On unsupported platforms, all calls are no-ops.
 */
public interface SentryMetrics {
    /**
     * Increments the counter [name] by [value]. Negative values are ignored.
     */
    public fun count(
        name: String,
        value: Long = 1,
        attributes: SentryAttributes.() -> Unit = {},
    )

    /**
     * Records the current [value] of the gauge [name]. NaN and infinite values are ignored.
     */
    public fun gauge(
        name: String,
        value: Double,
        unit: String? = null,
        attributes: SentryAttributes.() -> Unit = {},
    )

    /**
     * Records [value] as a sample of the distribution [name]. NaN and infinite values are ignored.
     */
    public fun distribution(
        name: String,
        value: Double,
        unit: String? = null,
        attributes: SentryAttributes.() -> Unit = {},
    )
}
