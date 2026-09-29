package io.sentry.kotlin.multiplatform.metrics

import io.sentry.kotlin.multiplatform.SentryAttributes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SentryMetricsTest {
    private data class Sent(
        val type: SentryMetricType,
        val name: String,
        val value: Double,
        val unit: String?,
        val attributes: Map<String, Any> = emptyMap(),
    )

    private class RecordingMetrics : BaseSentryMetrics() {
        val sent = mutableListOf<Sent>()

        override fun sendCount(
            name: String,
            value: Long,
            attributes: SentryAttributes,
        ) {
            sent += Sent(SentryMetricType.COUNTER, name, value.toDouble(), null, attributes.rawValues())
        }

        override fun sendGauge(
            name: String,
            value: Double,
            unit: String?,
            attributes: SentryAttributes,
        ) {
            sent += Sent(SentryMetricType.GAUGE, name, value, unit, attributes.rawValues())
        }

        override fun sendDistribution(
            name: String,
            value: Double,
            unit: String?,
            attributes: SentryAttributes,
        ) {
            sent += Sent(SentryMetricType.DISTRIBUTION, name, value, unit, attributes.rawValues())
        }
    }

    private val metrics = RecordingMetrics()

    @Test
    fun `count defaults to one`() {
        metrics.count("clicks")

        assertEquals(Sent(SentryMetricType.COUNTER, "clicks", 1.0, null), metrics.sent.single())
    }

    @Test
    fun `metrics forward value unit and attributes`() {
        metrics.count("clicks", 3) { this["screen"] = "home" }
        metrics.gauge("queue.size", -2.5, "item")
        metrics.distribution("request.duration", 12.5, "millisecond") { this["cached"] = true }

        assertEquals(
            listOf(
                Sent(SentryMetricType.COUNTER, "clicks", 3.0, null, mapOf("screen" to "home")),
                Sent(SentryMetricType.GAUGE, "queue.size", -2.5, "item"),
                Sent(SentryMetricType.DISTRIBUTION, "request.duration", 12.5, "millisecond", mapOf("cached" to true)),
            ),
            metrics.sent,
        )
    }

    @Test
    fun `invalid values are ignored`() {
        metrics.count("negative", -1)
        for (value in listOf(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)) {
            metrics.gauge("gauge", value)
            metrics.distribution("distribution", value)
        }

        assertTrue(metrics.sent.isEmpty())
    }

    @Test
    fun `isValid rejects non-finite values and negative counters`() {
        assertTrue(metric(SentryMetricType.COUNTER, 0.0).isValid())
        assertTrue(metric(SentryMetricType.GAUGE, -1.0).isValid())
        assertFalse(metric(SentryMetricType.COUNTER, -1.0).isValid())
        assertFalse(metric(SentryMetricType.GAUGE, Double.NaN).isValid())
        assertFalse(metric(SentryMetricType.DISTRIBUTION, Double.POSITIVE_INFINITY).isValid())
    }

    private fun metric(
        type: SentryMetricType,
        value: Double,
    ) = SentryMetric(0.0, type, "metric", value, null, SentryAttributes.empty(), "trace", null)
}

private fun SentryAttributes.rawValues(): Map<String, Any> = mapValues { it.value.value }
