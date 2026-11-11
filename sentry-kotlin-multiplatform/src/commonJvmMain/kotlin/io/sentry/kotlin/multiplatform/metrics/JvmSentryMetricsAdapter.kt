package io.sentry.kotlin.multiplatform.metrics

import io.sentry.kotlin.multiplatform.SentryAttributes
import io.sentry.kotlin.multiplatform.toJvmSentryAttributes
import io.sentry.metrics.IMetricsApi
import io.sentry.metrics.SentryMetricsParameters

/**
 * Adapter that bridges KMP [SentryMetrics] to the Java SDK's [IMetricsApi].
 */
internal class JvmSentryMetricsAdapter(
    private val jvmMetricsProvider: () -> IMetricsApi,
) : BaseSentryMetrics() {
    override fun sendCount(
        name: String,
        value: Long,
        attributes: SentryAttributes,
    ) {
        jvmMetricsProvider().count(name, value.toDouble(), null, attributes.toParameters())
    }

    override fun sendGauge(
        name: String,
        value: Double,
        unit: String?,
        attributes: SentryAttributes,
    ) {
        jvmMetricsProvider().gauge(name, value, unit, attributes.toParameters())
    }

    override fun sendDistribution(
        name: String,
        value: Double,
        unit: String?,
        attributes: SentryAttributes,
    ) {
        jvmMetricsProvider().distribution(name, value, unit, attributes.toParameters())
    }

    private fun SentryAttributes.toParameters() = SentryMetricsParameters.create(toJvmSentryAttributes())
}
