package io.sentry.kotlin.multiplatform.metrics

/**
 * Options for Sentry metrics.
 */
public class SentryMetricOptions {
    /**
     * A callback that is invoked before a metric is sent to Sentry.
     *
     * Modify the supplied metric in place and return that same instance to send it.
     * Returning null or a different instance drops the metric, as does an invalid [SentryMetric.value].
     */
    public var beforeSend: ((SentryMetric) -> SentryMetric?)? = null
}
