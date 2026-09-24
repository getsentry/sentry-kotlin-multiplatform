package io.sentry.kotlin.multiplatform.metrics

import cocoapods.sentryCocoa.SentryKMPMetric
import cocoapods.sentryCocoa.SentryKMPMetrics
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.toKmpSentryAttributes

internal fun SentryKMPMetric.toKmpMetric(): SentryMetric? {
    val metricValue =
        when (type()) {
            "counter" -> SentryMetricValue.Counter(doubleValue())
            "gauge" -> SentryMetricValue.Gauge(doubleValue())
            "distribution" -> SentryMetricValue.Distribution(doubleValue())
            else -> return null
        }
    val converted = attributes().toKmpSentryAttributes()
    return SentryMetric(timestamp(), name(), metricValue, unit(), converted, traceId(), spanId())
}

internal fun SentryKMPMetric.updateFrom(metric: SentryMetric): Boolean {
    setName(metric.name)
    setUnit(metric.unit)
    when (val number = metric.value) {
        is SentryMetricValue.Counter -> if (!setCounter(number.value)) return false
        is SentryMetricValue.Gauge -> setGauge(number.value)
        is SentryMetricValue.Distribution -> setDistribution(number.value)
    }
    setAttributes(metric.attributes.toCocoaMetricAttributes())
    return true
}

internal fun CocoaSentryOptions.applyMetricsOptions(options: SentryMetricOptions) {
    SentryKMPMetrics.configure(
        this as objcnames.classes.SentryOptions,
        options.beforeSend?.let { callback ->
            { nativeMetric ->
                nativeMetric?.applyCallback(callback)
            }
        },
    )
}

private fun SentryKMPMetric.applyCallback(callback: (SentryMetric) -> SentryMetric?): SentryKMPMetric? {
    val metric = toKmpMetric() ?: return null
    val result = applyMetricCallback(callback, metric) ?: return null
    if (!updateFrom(result)) return null
    return this
}
