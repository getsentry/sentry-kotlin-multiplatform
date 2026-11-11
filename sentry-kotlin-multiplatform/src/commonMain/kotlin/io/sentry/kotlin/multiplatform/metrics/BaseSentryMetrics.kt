package io.sentry.kotlin.multiplatform.metrics

import io.sentry.kotlin.multiplatform.SentryAttributes

/**
 * Base implementation of [SentryMetrics] that validates values and builds attributes.
 *
 * Platform implementations only forward valid metrics to their native SDK.
 */
internal abstract class BaseSentryMetrics : SentryMetrics {
    protected abstract fun sendCount(
        name: String,
        value: Long,
        attributes: SentryAttributes,
    )

    protected abstract fun sendGauge(
        name: String,
        value: Double,
        unit: String?,
        attributes: SentryAttributes,
    )

    protected abstract fun sendDistribution(
        name: String,
        value: Double,
        unit: String?,
        attributes: SentryAttributes,
    )

    override fun count(
        name: String,
        value: Long,
        attributes: SentryAttributes.() -> Unit,
    ) {
        if (value < 0) return
        sendCount(name, value, SentryAttributes.empty().apply(attributes))
    }

    override fun gauge(
        name: String,
        value: Double,
        unit: String?,
        attributes: SentryAttributes.() -> Unit,
    ) {
        if (!value.isFinite()) return
        sendGauge(name, value, unit, SentryAttributes.empty().apply(attributes))
    }

    override fun distribution(
        name: String,
        value: Double,
        unit: String?,
        attributes: SentryAttributes.() -> Unit,
    ) {
        if (!value.isFinite()) return
        sendDistribution(name, value, unit, SentryAttributes.empty().apply(attributes))
    }
}
