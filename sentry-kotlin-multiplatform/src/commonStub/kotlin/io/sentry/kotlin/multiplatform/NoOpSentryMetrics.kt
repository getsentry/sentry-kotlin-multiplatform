package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.metrics.SentryMetrics

/**
 * No-op implementation of [SentryMetrics] for stub/unsupported platforms.
 */
internal object NoOpSentryMetrics : SentryMetrics {
    override fun count(
        name: String,
        value: Long,
        attributes: SentryAttributes.() -> Unit,
    ) {
        // No-op
    }

    override fun gauge(
        name: String,
        value: Double,
        unit: String?,
        attributes: SentryAttributes.() -> Unit,
    ) {
        // No-op
    }

    override fun distribution(
        name: String,
        value: Double,
        unit: String?,
        attributes: SentryAttributes.() -> Unit,
    ) {
        // No-op
    }
}
