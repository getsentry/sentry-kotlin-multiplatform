package io.sentry.kotlin.multiplatform.metrics

import io.sentry.SentryAttributeType
import io.sentry.SentryLogEventAttributeValue
import io.sentry.SentryMetricsEvent
import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.SentryAttributeValue
import io.sentry.kotlin.multiplatform.SentryAttributes

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
    val converted = SentryAttributes.empty()
    attributes?.forEach { (key, attribute) ->
        when (attribute.type) {
            "string" -> converted[key] = attribute.value as String
            "boolean" -> converted[key] = attribute.value as Boolean
            "integer" -> converted[key] = (attribute.value as Number).toLong()
            "double" -> converted[key] = (attribute.value as Number).toDouble()
        }
    }
    return SentryMetric(timestamp, name, metricValue, unit, converted, traceId.toString(), spanId?.toString())
}

internal fun SentryMetricsEvent.updateFrom(
    metric: SentryMetric,
    originalAttributes: SentryAttributes,
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
    (originalAttributes.keys - metric.attributes.keys).forEach { attributes?.remove(it) }
    metric.attributes.forEach { (key, attribute) ->
        val nativeType =
            when (attribute) {
                is SentryAttributeValue.StringValue -> SentryAttributeType.STRING
                is SentryAttributeValue.BooleanValue -> SentryAttributeType.BOOLEAN
                is SentryAttributeValue.LongValue -> SentryAttributeType.INTEGER
                is SentryAttributeValue.DoubleValue -> SentryAttributeType.DOUBLE
            }
        setAttribute(key, SentryLogEventAttributeValue(nativeType, attribute.value))
    }
}

internal fun JvmSentryOptions.applyMetricsOptions(options: SentryMetricOptions) {
    this.metrics.isEnabled = options.enabled
    this.metrics.setBeforeSend(
        options.beforeSend?.let { callback ->
            io.sentry.SentryOptions.Metrics.BeforeSendMetricCallback { nativeMetric, _ ->
                nativeMetric.toKmpMetric()?.let { metric ->
                    val originalAttributes = metric.attributes.copy()
                    applyMetricCallback(callback, metric)?.let { result ->
                        nativeMetric.updateFrom(result, originalAttributes)
                        nativeMetric
                    }
                }
            }
        },
    )
}
