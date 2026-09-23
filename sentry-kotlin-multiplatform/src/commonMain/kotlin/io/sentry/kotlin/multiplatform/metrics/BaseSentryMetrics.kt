package io.sentry.kotlin.multiplatform.metrics

import io.sentry.kotlin.multiplatform.SentryAttributes

internal abstract class BaseSentryMetrics : SentryMetrics {
    override fun count(
        name: String,
        value: Long,
        configure: SentryAttributes.() -> Unit,
    ) {
        record(name, SentryMetricValue.Counter(value), null, configure)
    }

    override fun gauge(
        name: String,
        value: Double,
        unit: String?,
        configure: SentryAttributes.() -> Unit,
    ) {
        record(name, SentryMetricValue.Gauge(value), unit, configure)
    }

    override fun distribution(
        name: String,
        value: Double,
        unit: String?,
        configure: SentryAttributes.() -> Unit,
    ) {
        record(name, SentryMetricValue.Distribution(value), unit, configure)
    }

    private fun record(
        name: String,
        value: SentryMetricValue,
        unit: String?,
        configure: SentryAttributes.() -> Unit,
    ) {
        if (!value.isValid()) return
        val attributes = SentryAttributes.empty().apply(configure)
        capture(name, value, unit, attributes)
    }

    protected abstract fun capture(
        name: String,
        value: SentryMetricValue,
        unit: String?,
        attributes: SentryAttributes,
    )
}
