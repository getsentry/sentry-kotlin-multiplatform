package io.sentry.kotlin.multiplatform.metrics

import cocoapods.sentryCocoa.SentryKMPMetrics
import io.sentry.kotlin.multiplatform.SentryAttributes
import io.sentry.kotlin.multiplatform.toCocoaAttributes

/**
 * Adapter that bridges KMP [SentryMetrics] to the Cocoa SDK's Swift-only metrics API.
 */
internal class CocoaSentryMetricsAdapter : BaseSentryMetrics() {
    override fun sendCount(
        name: String,
        value: Long,
        attributes: SentryAttributes,
    ) {
        SentryKMPMetrics.count(name, value.toULong(), attributes.toCocoaAttributes())
    }

    override fun sendGauge(
        name: String,
        value: Double,
        unit: String?,
        attributes: SentryAttributes,
    ) {
        SentryKMPMetrics.gauge(name, value, unit, attributes.toCocoaAttributes())
    }

    override fun sendDistribution(
        name: String,
        value: Double,
        unit: String?,
        attributes: SentryAttributes,
    ) {
        SentryKMPMetrics.distribution(name, value, unit, attributes.toCocoaAttributes())
    }
}
