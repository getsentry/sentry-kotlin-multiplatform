package io.sentry.kotlin.multiplatform.metrics

import cocoapods.Sentry.SentryAttribute
import cocoapods.sentryCocoa.SentryKMPMetric
import cocoapods.sentryCocoa.SentryKMPMetrics
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.extensions.applyCocoaBaseOptions
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import objcnames.classes.SentryOptions as ObjCSentryOptions

class CocoaSentryMetricsTest {
    @AfterTest
    fun tearDown() {
        Sentry.close()
    }

    @Test
    fun `beforeSend receives recorded metrics`() {
        val received = mutableListOf<SentryMetric>()
        start { metric ->
            received += metric
            null
        }

        Sentry.metrics.count("clicks", 3) { this["screen"] = "home" }
        Sentry.metrics.gauge("queue.size", 2.0, "item")
        Sentry.metrics.distribution("request.duration", 12.5, "millisecond")

        assertEquals(listOf(SentryMetricType.COUNTER, SentryMetricType.GAUGE, SentryMetricType.DISTRIBUTION), received.map { it.type })
        assertEquals(listOf(3.0, 2.0, 12.5), received.map { it.value })
        assertEquals(listOf(null, "item", "millisecond"), received.map { it.unit })
        assertEquals("home", received[0].attributes["screen"]?.stringOrNull)
        assertEquals(32, received[0].traceId.length)
    }

    @Test
    fun `beforeSend changes are applied to the native metric`() {
        val native = assertNotNull(captureNativeMetric { Sentry.metrics.count("clicks") { this["removed"] = "old" } })

        val result =
            native.applyBeforeSend { metric ->
                metric.name = "renamed"
                metric.value = 2.0
                metric.unit = "custom"
                metric.attributes.remove("removed")
                metric.attributes["added"] = true
                metric
            }

        assertSame(native, result)
        val updated = assertNotNull(native.toKmpSentryMetric())
        assertEquals("renamed", updated.name)
        assertEquals(2.0, updated.value)
        assertEquals("custom", updated.unit)
        assertFalse(updated.attributes.containsKey("removed"))
        assertEquals(true, updated.attributes["added"]?.booleanOrNull)
    }

    @Test
    fun `beforeSend drops metrics when it returns null or throws or sets an invalid value`() {
        val native = assertNotNull(captureNativeMetric { Sentry.metrics.count("clicks") })

        assertNull(native.applyBeforeSend { null })
        assertNull(native.applyBeforeSend { error("callback failure") })
        assertNull(native.applyBeforeSend { metric -> metric.apply { value = 0.5 } })
    }

    @Test
    fun `beforeSend keeps unchanged counters above the exact Double range`() {
        // arm64_32 watchOS can't record counters above UInt32.MAX, so there is nothing to check there.
        val native = captureNativeMetric { SentryKMPMetrics.count("large", ULong.MAX_VALUE, emptyMap<Any?, SentryAttribute>()) } ?: return

        assertSame(native, native.applyBeforeSend { it })
    }

    private fun start(beforeSend: (SentryMetric) -> SentryMetric?) {
        Sentry.initWithPlatformOptions {
            it.applyCocoaBaseOptions(
                SentryOptions().apply {
                    dsn = "http://public@127.0.0.1:9/1"
                    metrics.beforeSend = beforeSend
                },
            )
            it.setEnableAutoSessionTracking(false)
        }
    }

    private fun captureNativeMetric(record: () -> Unit): SentryKMPMetric? {
        var captured: SentryKMPMetric? = null
        Sentry.initWithPlatformOptions {
            it.setDsn("http://public@127.0.0.1:9/1")
            it.setEnableAutoSessionTracking(false)
            SentryKMPMetrics.setBeforeSend(it as ObjCSentryOptions) { metric ->
                captured = metric
                null
            }
        }
        record()
        return captured
    }
}
