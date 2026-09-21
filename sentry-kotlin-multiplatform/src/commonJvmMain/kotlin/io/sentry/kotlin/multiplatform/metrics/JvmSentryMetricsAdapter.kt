package io.sentry.kotlin.multiplatform.metrics

import io.sentry.kotlin.multiplatform.SentryAttributes
import io.sentry.kotlin.multiplatform.log.toJvmSentryAttributes
import io.sentry.metrics.IMetricsApi
import io.sentry.metrics.SentryMetricsParameters

internal class JvmSentryMetricsAdapter(
    private val provider: () -> IMetricsApi,
) : BaseSentryMetrics() {
    override fun capture(
        name: String,
        value: SentryMetricValue,
        unit: String?,
        attributes: SentryAttributes,
    ) {
        val params = SentryMetricsParameters.create(attributes.toJvmSentryAttributes())
        val metrics = provider()
        when (value) {
            is SentryMetricValue.Counter -> metrics.count(name, value.value.toDouble(), unit, params)
            is SentryMetricValue.Gauge -> metrics.gauge(name, value.value, unit, params)
            is SentryMetricValue.Distribution -> metrics.distribution(name, value.value, unit, params)
        }
    }
}
