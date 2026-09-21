package io.sentry.kotlin.multiplatform.metrics

/** Records trace-connected metrics using the native SDK. Unsupported platforms are no-ops. */
public interface SentryMetrics {
    /**
     * Records an integer increment, defaulting to one. Values outside 0..2^53 are ignored
     * to avoid unsigned wrapping on Apple or loss of precision on Java/Android.
     * On 32-bit watchOS, values above UInt.MAX_VALUE are also ignored.
     */
    public fun count(
        name: String,
        value: Long = 1,
        configure: SentryMetricBuilder.() -> Unit = {},
    )

    /** Records a finite current value. NaN and infinities are ignored. Units may be custom strings. */
    public fun gauge(
        name: String,
        value: Double,
        unit: String? = null,
        configure: SentryMetricBuilder.() -> Unit = {},
    )

    /** Records a finite distribution sample. NaN and infinities are ignored. Units may be custom strings. */
    public fun distribution(
        name: String,
        value: Double,
        unit: String? = null,
        configure: SentryMetricBuilder.() -> Unit = {},
    )
}
