package io.sentry.kotlin.multiplatform.metrics

import io.sentry.Hint
import io.sentry.SentryAttributeType
import io.sentry.SentryEnvelope
import io.sentry.SentryItemType
import io.sentry.SentryLogEventAttributeValue
import io.sentry.SentryMetricsEvent
import io.sentry.SentryMetricsEvents
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.extensions.applyJvmBaseOptions
import io.sentry.protocol.SentryId
import io.sentry.transport.ITransport
import io.sentry.transport.RateLimiter
import java.util.Collections
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import io.sentry.Sentry as JvmSentry
import io.sentry.SentryOptions as JvmSentryOptions

class JvmSentryMetricsTest {
    @AfterTest
    fun tearDown() {
        JvmSentry.close()
    }

    @Test
    fun `beforeSend receives recorded metrics`() {
        val received = mutableListOf<SentryMetric>()
        JvmSentry.init(
            jvmOptions { metric ->
                received += metric
                null
            },
        )

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
    fun `beforeSend changes are applied and non-scalar attributes are kept`() {
        val native = nativeMetric()
        native.setAttribute("removed", SentryLogEventAttributeValue(SentryAttributeType.STRING, "old"))
        native.setAttribute("array", SentryLogEventAttributeValue(SentryAttributeType.ARRAY, listOf("kept")))
        val options =
            jvmOptions { metric ->
                metric.name = "renamed"
                metric.value = 2.0
                metric.unit = "custom"
                metric.attributes.remove("removed")
                metric.attributes["added"] = true
                metric
            }

        assertSame(native, options.metrics.beforeSend?.execute(native, Hint()))
        assertEquals("renamed", native.name)
        assertEquals(2.0, native.value)
        assertEquals("custom", native.unit)
        val attributes = assertNotNull(native.attributes)
        assertFalse(attributes.containsKey("removed"))
        assertEquals(true, attributes["added"]?.value)
        assertEquals(listOf("kept"), attributes["array"]?.value)
    }

    @Test
    fun `beforeSend drops metrics when it returns null or an invalid value`() {
        val dropAll = jvmOptions { null }
        val makeNegative = jvmOptions { metric -> metric.apply { value = -1.0 } }

        assertNull(dropAll.metrics.beforeSend?.execute(nativeMetric(), Hint()))
        assertNull(makeNegative.metrics.beforeSend?.execute(nativeMetric(), Hint()))
    }

    @Test
    fun `beforeSend keeps metrics of unknown types unchanged`() {
        var calls = 0
        val options =
            jvmOptions {
                calls++
                null
            }
        val native = nativeMetric(type = "future")

        assertSame(native, options.metrics.beforeSend?.execute(native, Hint()))
        assertEquals(0, calls)
    }

    @Test
    fun `sent metrics are connected to the active trace`() {
        val envelopes = Collections.synchronizedList(mutableListOf<SentryEnvelope>())
        val options =
            jvmOptions { it }.apply {
                tracesSampleRate = 1.0
                setTransportFactory { _, _ -> RecordingTransport(envelopes) }
            }
        JvmSentry.init(options)
        val transaction = JvmSentry.startTransaction("metrics", "test")
        JvmSentry.configureScope { it.setTransaction(transaction) }

        Sentry.metrics.count("clicks")
        JvmSentry.flush(5_000)

        val sent =
            envelopes
                .flatMap { it.items }
                .filter { it.header.type == SentryItemType.TraceMetric }
                .flatMap { item ->
                    val events = options.serializer.deserialize(item.data.decodeToString().reader(), SentryMetricsEvents::class.java)
                    assertNotNull(events).items
                }.single()
        assertEquals("clicks", sent.name)
        assertEquals(transaction.spanContext.traceId, sent.traceId)
        assertEquals(transaction.spanContext.spanId, sent.spanId)
        transaction.finish()
    }

    private fun jvmOptions(beforeSend: (SentryMetric) -> SentryMetric?) =
        JvmSentryOptions().apply {
            applyJvmBaseOptions(
                SentryOptions().apply {
                    dsn = "http://public@127.0.0.1:9/1"
                    metrics.beforeSend = beforeSend
                },
            )
            isEnableAutoSessionTracking = false
        }

    private fun nativeMetric(type: String = "counter") = SentryMetricsEvent(SentryId(), 0.0, "metric", type, 1.0)

    private class RecordingTransport(
        private val envelopes: MutableList<SentryEnvelope>,
    ) : ITransport {
        override fun send(
            envelope: SentryEnvelope,
            hint: Hint,
        ) {
            envelopes += envelope
        }

        override fun flush(timeoutMillis: Long) = Unit

        override fun getRateLimiter(): RateLimiter? = null

        override fun close() = Unit

        override fun close(isRestarting: Boolean) = Unit
    }
}
