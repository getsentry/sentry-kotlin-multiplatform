package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.metrics.SentryMetrics

internal object NoOpSentryMetrics : SentryMetrics {
    override fun count(
        name: String,
        value: Long,
        configure: SentryAttributes.() -> Unit,
    ) = Unit

    override fun gauge(
        name: String,
        value: Double,
        unit: String?,
        configure: SentryAttributes.() -> Unit,
    ) = Unit

    override fun distribution(
        name: String,
        value: Double,
        unit: String?,
        configure: SentryAttributes.() -> Unit,
    ) = Unit
}
