package io.sentry.kotlin.multiplatform.metrics

import io.sentry.kotlin.multiplatform.SentryAttributes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SentryMetricsTest {
    private class Recorder : BaseSentryMetrics() {
        val records = mutableListOf<SentryMetric>()

        override fun capture(
            name: String,
            value: SentryMetricValue,
            unit: String?,
            attributes: SentryAttributes,
        ) {
            records += SentryMetric(0.0, name, value, unit, attributes, "trace")
        }
    }

    @Test
    fun `records all operations with independent attributes and custom units`() {
        val metrics = Recorder()
        metrics.count("count") { attributes["count"] = 42L }
        metrics.gauge("gauge", -2.5, "connection") { attributes { this["enabled"] = true } }
        metrics.distribution("distribution", 0.5, "millisecond")
        assertEquals(
            listOf(
                SentryMetricValue.Counter(1),
                SentryMetricValue.Gauge(-2.5),
                SentryMetricValue.Distribution(0.5),
            ),
            metrics.records.map {
                it.value
            },
        )
        assertEquals(42L, metrics.records[0].attributes["count"]?.longOrNull)
        assertEquals(true, metrics.records[1].attributes["enabled"]?.booleanOrNull)
        assertEquals("connection", metrics.records[1].unit)
        assertEquals("millisecond", metrics.records[2].unit)
        assertTrue(metrics.records[2].attributes.isEmpty())
    }

    @Test
    fun `invalid numbers are ignored before evaluating the builder`() {
        val metrics = Recorder()
        val unexpected: SentryMetricBuilder.() -> Unit = { error("must not build invalid metric") }
        metrics.count("negative", -1, unexpected)
        metrics.count("imprecise", MAX_EXACT_COUNTER + 1, unexpected)
        metrics.count("overflow", Long.MAX_VALUE, unexpected)
        listOf(Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).forEach {
            metrics.gauge("invalid", it, configure = unexpected)
            metrics.distribution("invalid", it, configure = unexpected)
        }
        assertTrue(metrics.records.isEmpty())
        metrics.count("zero", 0)
        metrics.count("max", MAX_EXACT_COUNTER)
        assertEquals(listOf(SentryMetricValue.Counter(0), SentryMetricValue.Counter(MAX_EXACT_COUNTER)), metrics.records.map { it.value })
    }

    @Test
    fun `callback failure and invalid replacement drop the metric`() {
        val metric = SentryMetric(0.0, "metric", SentryMetricValue.Counter(1), traceId = "trace")
        assertNull(applyMetricCallback({ throw IllegalStateException("callback") }, metric))
        assertNull(applyMetricCallback({ null }, metric))
        assertNull(
            applyMetricCallback({
                it.value = SentryMetricValue.Counter(-1)
                it
            }, metric),
        )
        assertTrue(SentryMetricOptions().enabled)
        assertNull(SentryMetricOptions().beforeSend)
    }
}
