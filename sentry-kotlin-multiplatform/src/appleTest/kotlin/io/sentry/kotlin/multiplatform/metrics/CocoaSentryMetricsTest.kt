package io.sentry.kotlin.multiplatform.metrics

import cocoapods.sentryCocoa.SentryKMPMetric
import cocoapods.sentryCocoa.SentryKMPMetrics
import io.sentry.kotlin.multiplatform.CocoaSentryOptions
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryAttributes
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.extensions.applyCocoaBaseOptions
import kotlinx.cinterop.convert
import platform.darwin.NSInteger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CocoaSentryMetricsTest {
    @BeforeTest fun reset() {
        Sentry.close()
    }

    @AfterTest fun close() {
        Sentry.close()
    }

    private fun start(
        enabled: Boolean = true,
        callback: (SentryMetric) -> SentryMetric?,
    ) {
        Sentry.initWithPlatformOptions {
            it.applyCocoaBaseOptions(
                SentryOptions().apply {
                    dsn = "http://public@127.0.0.1:9/1"
                    metrics.enabled = enabled
                    metrics.beforeSend = callback
                },
            )
            it.setEnableAutoSessionTracking(false)
            it.setEnableAppHangTracking(false)
        }
    }

    @Test
    fun `retained facade records typed native metrics and observes lifecycle changes`() {
        val metrics = Sentry.metrics
        val captured = mutableListOf<SentryMetric>()
        val callback: (SentryMetric) -> SentryMetric? = {
            captured += it
            null
        }
        metrics.count("before init")
        start(callback = callback)
        metrics.count("count", 3) {
            attributes["bool"] = true
            attributes["integer"] = 42L
            attributes["double"] = 1.5
            attributes["string"] =
                "text"
        }
        metrics.gauge("gauge", -2.0, "connection")
        metrics.distribution("distribution", 12.5, "millisecond")
        assertEquals(
            listOf(
                SentryMetricValue.Counter(3),
                SentryMetricValue.Gauge(-2.0),
                SentryMetricValue.Distribution(12.5),
            ),
            captured.map {
                it.value
            },
        )
        assertEquals(true, captured[0].attributes["bool"]?.booleanOrNull)
        assertEquals(42L, captured[0].attributes["integer"]?.longOrNull)
        assertEquals(1.5, captured[0].attributes["double"]?.doubleOrNull)
        assertEquals("text", captured[0].attributes["string"]?.stringOrNull)
        assertEquals("connection", captured[1].unit)
        assertEquals("millisecond", captured[2].unit)
        assertTrue(captured.all { it.traceId.length == 32 })
        Sentry.close()
        metrics.count("closed")
        start(false, callback)
        metrics.count("disabled")
        assertEquals(3, captured.size)
        start(callback = callback)
        metrics.count("restarted")
        assertEquals(4, captured.size)
    }

    @Test
    fun `native wrapper applies replacement fields and attribute removals preserving metadata`() {
        var captured: SentryKMPMetric? = null
        Sentry.initWithPlatformOptions {
            it.setDsn("http://public@127.0.0.1:9/1")
            it.setEnableAutoSessionTracking(false)
            SentryKMPMetrics.configure(it as objcnames.classes.SentryOptions) { metric ->
                captured = metric
                null
            }
        }
        Sentry.metrics.count("original") { attributes["remove"] = "old" }
        val native = assertNotNull(captured)
        val timestamp = native.timestamp()
        val traceId = native.traceId()
        native.updateFrom(
            SentryMetric(
                999.0,
                "replacement",
                SentryMetricValue.Distribution(2.5),
                "custom",
                SentryAttributes.of(
                    "bool" to true,
                    "long" to 42L,
                ),
                "ignored",
            ),
        )
        val result = assertNotNull(native.toKmpMetric())
        assertEquals("replacement", result.name)
        assertEquals(SentryMetricValue.Distribution(2.5), result.value)
        assertEquals("custom", result.unit)
        assertEquals(timestamp, result.timestamp)
        assertEquals(traceId, result.traceId)
        assertFalse(result.attributes.containsKey("remove"))
        assertEquals(true, result.attributes["bool"]?.booleanOrNull)
        assertEquals(42L, result.attributes["long"]?.longOrNull)
    }

    @Test
    fun `native options apply defaults and clear stale callbacks`() {
        val native = CocoaSentryOptions()
        native.applyCocoaBaseOptions(SentryOptions().apply { metrics.enabled = false })
        assertFalse(native.enableMetrics())
        native.applyCocoaBaseOptions(SentryOptions())
        assertTrue(native.enableMetrics())
        var calls = 0
        Sentry.initWithPlatformOptions {
            it.applyCocoaBaseOptions(
                SentryOptions().apply {
                    metrics.beforeSend = {
                        calls++
                        null
                    }
                },
            )
            it.applyCocoaBaseOptions(SentryOptions())
            it.setDsn("http://public@127.0.0.1:9/1")
            it.setEnableAutoSessionTracking(false)
        }
        Sentry.metrics.count("cleared")
        assertEquals(0, calls)
    }

    @Test
    fun `callback exception does not cross the Swift boundary`() {
        var calls = 0
        start {
            calls++
            error("callback failure")
        }
        Sentry.metrics.count("metric")
        assertEquals(1, calls)
    }

    @Test
    fun `watchOS integer boundaries cannot wrap or trap`() {
        val is32Bit = Long.MAX_VALUE.convert<NSInteger>().toLong() != Long.MAX_VALUE
        val captured = mutableListOf<SentryMetric>()
        start {
            captured += it
            null
        }
        Sentry.metrics.count("large", MAX_EXACT_COUNTER)
        Sentry.metrics.count("native boundary", UInt.MAX_VALUE.toLong()) {
            attributes["large"] = Long.MAX_VALUE
            attributes["small"] = 42L
        }
        assertEquals(if (is32Bit) 1 else 2, captured.size)
        assertEquals(SentryMetricValue.Counter(UInt.MAX_VALUE.toLong()), captured.last().value)
        assertEquals(42L, captured.last().attributes["small"]?.longOrNull)
        assertEquals(if (is32Bit) null else Long.MAX_VALUE, captured.last().attributes["large"]?.longOrNull)
    }
}
