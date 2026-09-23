package io.sentry.kotlin.multiplatform.metrics

import cocoapods.Sentry.SentryAttribute
import cocoapods.sentryCocoa.SentryKMPMetrics
import io.sentry.kotlin.multiplatform.SentryAttributeValue
import io.sentry.kotlin.multiplatform.SentryAttributes
import kotlinx.cinterop.convert
import platform.darwin.NSInteger

internal class CocoaSentryMetricsAdapter : BaseSentryMetrics() {
    override fun capture(
        name: String,
        value: SentryMetricValue,
        unit: String?,
        attributes: SentryAttributes,
    ) {
        val nativeAttributes = attributes.toCocoaMetricAttributes()
        when (value) {
            is SentryMetricValue.Counter -> SentryKMPMetrics.count(name, value.value.toULong(), nativeAttributes)
            is SentryMetricValue.Gauge -> SentryKMPMetrics.gauge(name, value.value, unit, nativeAttributes)
            is SentryMetricValue.Distribution ->
                SentryKMPMetrics.distribution(name, value.value, unit, nativeAttributes)
        }
    }
}

internal fun SentryAttributes.toCocoaMetricAttributes(): Map<Any?, SentryAttribute> =
    entries
        .mapNotNull { (key, attribute) ->
            attribute.toCocoaMetricAttribute()?.let { key as Any? to it }
        }.toMap()

private fun SentryAttributeValue.toCocoaMetricAttribute(): SentryAttribute? =
    when (this) {
        is SentryAttributeValue.StringValue -> SentryAttribute(string = value as String)
        is SentryAttributeValue.BooleanValue -> SentryAttribute(boolean = value as Boolean)
        is SentryAttributeValue.LongValue -> {
            val original = value as Long
            val native: NSInteger = original.convert()
            // arm64_32 watchOS cannot represent every shared Long attribute.
            if (native.toLong() == original) SentryAttribute(integer = native) else null
        }
        is SentryAttributeValue.DoubleValue -> SentryAttribute(double = value as Double)
    }
