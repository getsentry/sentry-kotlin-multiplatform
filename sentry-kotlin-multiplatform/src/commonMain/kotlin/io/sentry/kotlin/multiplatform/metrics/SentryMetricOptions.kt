package io.sentry.kotlin.multiplatform.metrics

/**
 * Options for Sentry metrics.
 */
public class SentryMetricOptions {
    /**
     * A callback that is invoked before a metric is sent to Sentry.
     *
     * Return the (potentially modified) metric to send it, or null to drop it.
     * Metrics whose [SentryMetric.value] is invalid after the callback are dropped.
     */
    public var beforeSend: ((SentryMetric) -> SentryMetric?)? = null
}
