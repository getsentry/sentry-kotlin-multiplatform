package io.sentry.kotlin.multiplatform.metrics

import cocoapods.sentryCocoa.SentryKMPMetric
import cocoapods.sentryCocoa.SentryKMPMetrics
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.toCocoaAttributes
import io.sentry.kotlin.multiplatform.toKmpSentryAttributes
import objcnames.classes.SentryOptions as ObjCSentryOptions

/**
 * Converts a Cocoa metric to a KMP [SentryMetric] for use in the beforeSend callback.
 * Returns null for metric types this SDK doesn't know.
 */
internal fun SentryKMPMetric.toKmpSentryMetric(): SentryMetric? {
    val type =
        when (type()) {
            "counter" -> SentryMetricType.COUNTER
            "gauge" -> SentryMetricType.GAUGE
            "distribution" -> SentryMetricType.DISTRIBUTION
            else -> return null
        }
    return SentryMetric(
        timestamp = timestamp(),
        type = type,
        name = name(),
        value = value(),
        unit = unit(),
        attributes = attributes().toKmpSentryAttributes(),
        traceId = traceId(),
        spanId = spanId(),
    )
}

/**
 * Updates this Cocoa metric from a KMP [SentryMetric].
 * Returns false if the value can't be stored natively, for example a fractional counter.
 */
internal fun SentryKMPMetric.updateFrom(kmpMetric: SentryMetric): Boolean {
    if (!replaceValue(kmpMetric.value)) return false
    setName(kmpMetric.name)
    setUnit(kmpMetric.unit)
    setAttributes(kmpMetric.attributes.toCocoaAttributes())
    return true
}

/**
 * Applies the KMP metrics options to these Cocoa options.
 */
internal fun CocoaSentryOptions.applyMetricsOptions(kmpOptions: SentryMetricOptions) {
    kmpOptions.beforeSend?.let { kmpBeforeSend ->
        SentryKMPMetrics.setBeforeSend(this as ObjCSentryOptions) { cocoaMetric ->
            cocoaMetric?.applyBeforeSend(kmpBeforeSend)
        }
    }
}

/**
 * Runs the KMP beforeSend callback on this Cocoa metric. Returns null to drop the metric.
 */
@Suppress("TooGenericExceptionCaught", "SwallowedException")
internal fun SentryKMPMetric.applyBeforeSend(beforeSend: (SentryMetric) -> SentryMetric?): SentryKMPMetric? {
    val kmpMetric = toKmpSentryMetric() ?: return this
    val result =
        try {
            beforeSend(kmpMetric)
        } catch (e: Throwable) {
            // Kotlin exceptions must not propagate into Swift. Drop the metric, as the Java SDK does.
            null
        }
    return if (result != null && result.isValid() && updateFrom(result)) this else null
}
