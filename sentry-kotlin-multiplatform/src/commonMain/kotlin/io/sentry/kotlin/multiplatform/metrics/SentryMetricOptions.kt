package io.sentry.kotlin.multiplatform.metrics

/** Options for trace-connected metrics. */
public class SentryMetricOptions {
    /** Whether metrics are enabled. Defaults to true, matching the native SDKs. */
    public var enabled: Boolean = true

    /**
     * Modifies or filters a native metric after enrichment. Return null to drop it.
     * Exceptions and invalid numeric values drop the metric. Unsupported native attribute
     * types and native timestamp/trace metadata are preserved when applying the result.
     */
    public var beforeSend: ((SentryMetric) -> SentryMetric?)? = null
}
