package io.sentry.kotlin.multiplatform.metrics

import io.sentry.SentryMetricsEvent
import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.toKmpSentryAttributes
import io.sentry.kotlin.multiplatform.updateJvmAttributes

internal fun SentryMetricsEvent.toKmpMetric(): SentryMetric? {
    val metricValue =
        when (type) {
            "counter" -> {
                if (!value.isFinite() || value % 1.0 != 0.0) return null
                if (value < 0 || value > MAX_EXACT_COUNTER.toDouble()) return null
                SentryMetricValue.Counter(value.toLong())
            }
            "gauge" -> SentryMetricValue.Gauge(value)
            "distribution" -> SentryMetricValue.Distribution(value)
            else -> return null
        }
    val converted = attributes.toKmpSentryAttributes()
    return SentryMetric(timestamp, name, metricValue, unit, converted, traceId.toString(), spanId?.toString())
}

internal fun SentryMetricsEvent.updateFrom(
    metric: SentryMetric,
    originalKeys: Set<String>,
) {
    name = metric.name
    unit = metric.unit
    when (val number = metric.value) {
        is SentryMetricValue.Counter -> {
            type = "counter"
            value = number.value.toDouble()
        }
        is SentryMetricValue.Gauge -> {
            type = "gauge"
            value = number.value
        }
        is SentryMetricValue.Distribution -> {
            type = "distribution"
            value = number.value
        }
    }
    updateJvmAttributes(metric.attributes, originalKeys, { attributes?.remove(it) }, ::setAttribute)
}

internal fun JvmSentryOptions.applyMetricsOptions(options: SentryMetricOptions) {
    this.metrics.setBeforeSend(
        options.beforeSend?.let { callback ->
            io.sentry.SentryOptions.Metrics.BeforeSendMetricCallback { nativeMetric, _ ->
                nativeMetric.applyCallback(callback)
            }
        },
    )
}

private fun SentryMetricsEvent.applyCallback(callback: (SentryMetric) -> SentryMetric?): SentryMetricsEvent? {
    val metric = toKmpMetric() ?: return null
    val originalKeys = metric.attributes.keys.toSet()
    val result = applyMetricCallback(callback, metric) ?: return null
    updateFrom(result, originalKeys)
    return this
}
