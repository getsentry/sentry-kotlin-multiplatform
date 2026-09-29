package io.sentry.kotlin.multiplatform.metrics

import io.sentry.SentryMetricsEvent
import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.isScalar
import io.sentry.kotlin.multiplatform.toJvmAttributeValue
import io.sentry.kotlin.multiplatform.toKmpSentryAttributes

/**
 * Converts a JVM metric to a KMP [SentryMetric] for use in the beforeSend callback.
 * Returns null for metric types this SDK doesn't know.
 */
internal fun SentryMetricsEvent.toKmpSentryMetric(): SentryMetric? {
    val kmpType =
        when (type) {
            "counter" -> SentryMetricType.COUNTER
            "gauge" -> SentryMetricType.GAUGE
            "distribution" -> SentryMetricType.DISTRIBUTION
            else -> return null
        }
    return SentryMetric(
        timestamp = timestamp,
        type = kmpType,
        name = name,
        value = value,
        unit = unit,
        attributes = attributes.toKmpSentryAttributes(),
        traceId = traceId.toString(),
        spanId = spanId?.toString(),
    )
}

/**
 * Applies the KMP metrics options to these JVM options.
 */
internal fun JvmSentryOptions.applyMetricsOptions(kmpOptions: SentryMetricOptions) {
    kmpOptions.beforeSend?.let { kmpBeforeSend ->
        metrics.setBeforeSend { jvmMetric, _ ->
            val kmpMetric = jvmMetric.toKmpSentryMetric() ?: return@setBeforeSend jvmMetric
            val result = kmpBeforeSend(kmpMetric)
            if (result != null && result.isValid()) {
                jvmMetric.updateFrom(result)
                jvmMetric
            } else {
                null
            }
        }
    }
}

/**
 * Updates this JVM metric from a KMP [SentryMetric].
 * Native attributes that KMP can't represent, such as arrays, are kept.
 */
internal fun SentryMetricsEvent.updateFrom(kmpMetric: SentryMetric) {
    name = kmpMetric.name
    value = kmpMetric.value
    unit = kmpMetric.unit
    attributes =
        attributes.orEmpty().filterValues { !it.isScalar }.toMutableMap().apply {
            kmpMetric.attributes.forEach { (key, value) -> put(key, value.toJvmAttributeValue()) }
        }
}
