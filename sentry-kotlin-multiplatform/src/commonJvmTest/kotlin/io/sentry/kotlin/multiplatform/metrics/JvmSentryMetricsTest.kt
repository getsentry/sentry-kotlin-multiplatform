package io.sentry.kotlin.multiplatform.metrics

import io.sentry.Hint
import io.sentry.SentryAttributeType
import io.sentry.SentryLogEventAttributeValue
import io.sentry.SentryMetricsEvent
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryAttributes
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.extensions.applyJvmBaseOptions
import io.sentry.protocol.SentryId
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import io.sentry.Sentry as NativeSentry
import io.sentry.SentryOptions as NativeOptions

class JvmSentryMetricsTest {
    @BeforeTest fun reset() {
        NativeSentry.close()
    }

    @AfterTest fun close() {
        NativeSentry.close()
    }

    @Test
    fun `native options apply and clear callbacks`() {
        val native = NativeOptions()
        val options =
            SentryOptions().apply {
                metrics.beforeSend = { null }
            }
        native.applyJvmBaseOptions(options)
        assertNotNull(native.metrics.beforeSend)
        native.applyJvmBaseOptions(SentryOptions())
        assertNull(native.metrics.beforeSend)
    }

    @Test
    fun `callback replacement applies fields but preserves native metadata and unknown attributes`() {
        val native = SentryMetricsEvent(SentryId(), 123.5, "original", "counter", 1.0)
        native.setAttribute("remove", SentryLogEventAttributeValue(SentryAttributeType.STRING, "old"))
        native.setAttribute("array", SentryLogEventAttributeValue(SentryAttributeType.ARRAY, listOf("keep")))
        val traceId = native.traceId
        val options =
            NativeOptions().apply {
                applyJvmBaseOptions(
                    SentryOptions().apply {
                        metrics.beforeSend = {
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
                            )
                        }
                    },
                )
            }
        assertSame(native, assertNotNull(options.metrics.beforeSend).execute(native, Hint()))
        assertEquals("replacement", native.name)
        assertEquals("distribution", native.type)
        assertEquals(2.5, native.value)
        assertEquals("custom", native.unit)
        assertEquals(123.5, native.timestamp)
        assertEquals(traceId, native.traceId)
        assertFalse(assertNotNull(native.attributes).containsKey("remove"))
        assertEquals(listOf("keep"), native.attributes?.get("array")?.value)
        assertEquals(true, native.attributes?.get("bool")?.value)
        assertEquals(42L, native.attributes?.get("long")?.value)
    }

    @Test
    fun `callback preserves fractional and large native counters`() {
        val values = listOf(0.5, MAX_EXACT_COUNTER.toDouble() + 2.0, Double.MAX_VALUE)
        val observed = mutableListOf<Double>()
        val options =
            NativeOptions().apply {
                applyJvmBaseOptions(
                    SentryOptions().apply {
                        metrics.beforeSend = {
                            observed += (it.value as SentryMetricValue.Counter).value
                            it.attributes["processed"] = true
                            it
                        }
                    },
                )
            }
        values.forEach { value ->
            val native = SentryMetricsEvent(SentryId(), 123.0, "native", "counter", value)
            assertSame(native, assertNotNull(options.metrics.beforeSend).execute(native, Hint()))
            assertEquals(value, native.value)
            assertEquals(true, native.attributes?.get("processed")?.value)
        }
        assertEquals(values, observed)
    }

    @Test
    fun `native callback drops null exceptions and invalid replacements`() {
        val native = SentryMetricsEvent(SentryId(), 0.0, "metric", "counter", 1.0)
        val callbacks: List<(SentryMetric) -> SentryMetric?> =
            listOf({ null }, { error("failure") }, {
                it.value =
                    SentryMetricValue.Gauge(Double.NaN)
                ; it
            })
        callbacks.forEach { callback ->
            val options = NativeOptions().apply { applyJvmBaseOptions(SentryOptions().apply { metrics.beforeSend = callback }) }
            assertNull(assertNotNull(options.metrics.beforeSend).execute(native, Hint()))
        }
    }

    @Test
    fun `retained facade records typed native metrics and observes lifecycle changes`() {
        val metrics = Sentry.metrics
        val captured = mutableListOf<SentryMetric>()
        metrics.count("before init")

        fun start() {
            NativeSentry.init(
                NativeOptions().apply {
                    applyJvmBaseOptions(
                        SentryOptions().apply {
                            dsn = "http://public@127.0.0.1:9/1"
                            this.metrics.beforeSend = {
                                captured += it
                                null
                            }
                        },
                    )
                    isEnableAutoSessionTracking = false
                },
            )
        }
        start()
        metrics.count("count", 3) {
            this["bool"] = true
            this["integer"] = 42L
            this["double"] = 1.5
            this["string"] =
                "text"
        }
        metrics.gauge("gauge", -2.0, "connection")
        metrics.distribution("distribution", 12.5, "millisecond")
        assertEquals(
            listOf(
                SentryMetricValue.Counter(3.0),
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
        NativeSentry.close()
        metrics.count("closed")
        assertEquals(3, captured.size)
        start()
        metrics.count("restarted")
        assertEquals(4, captured.size)
    }

    @Test
    fun `flush emits correlated metrics and omits dropped records`() {
        val envelopes = java.util.Collections.synchronizedList(mutableListOf<io.sentry.SentryEnvelope>())
        val transport =
            object : io.sentry.transport.ITransport {
                override fun send(
                    envelope: io.sentry.SentryEnvelope,
                    hint: Hint,
                ) {
                    envelopes += envelope
                }

                override fun flush(timeoutMillis: Long) = Unit

                override fun getRateLimiter(): io.sentry.transport.RateLimiter? = null

                override fun close() = Unit

                override fun close(isRestarting: Boolean) = Unit
            }
        val options =
            NativeOptions().apply {
                applyJvmBaseOptions(
                    SentryOptions().apply {
                        dsn = "http://public@127.0.0.1:9/1"
                        tracesSampleRate = 1.0
                        metrics.beforeSend = { metric ->
                            if (metric.name == "drop") {
                                null
                            } else {
                                metric.attributes.remove("secret")
                                metric.attributes["processed"] = true
                                metric
                            }
                        }
                    },
                )
                isEnableAutoSessionTracking = false
                setTransportFactory { _, _ -> transport }
            }
        NativeSentry.init(options)
        val transaction = NativeSentry.startTransaction("metrics", "test")
        NativeSentry.configureScope { it.setTransaction(transaction) }
        Sentry.metrics.count("count", 2) { this["secret"] = "remove" }
        Sentry.metrics.gauge("gauge", 4.5, "custom")
        Sentry.metrics.distribution("distribution", 3.0, "millisecond")
        NativeSentry.metrics().count("native fractional", 0.5)
        NativeSentry.metrics().count("native large", MAX_EXACT_COUNTER.toDouble() + 2.0)
        Sentry.metrics.count("drop")
        NativeSentry.flush(5_000)
        val events =
            synchronized(envelopes) {
                envelopes.flatMap { envelope ->
                    envelope.items.filter { it.header.type == io.sentry.SentryItemType.TraceMetric }.flatMap {
                        assertNotNull(
                            options.serializer.deserialize(it.data.decodeToString().reader(), io.sentry.SentryMetricsEvents::class.java),
                        ).items
                    }
                }
            }
        assertEquals(listOf("count", "gauge", "distribution", "native fractional", "native large"), events.map { it.name })
        events.forEach {
            assertEquals(transaction.spanContext.traceId, it.traceId)
            assertEquals(transaction.spanContext.spanId, it.spanId)
            assertEquals(true, it.attributes?.get("processed")?.value)
            assertFalse(it.attributes.orEmpty().containsKey("secret"))
        }
        assertEquals(listOf(2.0, 4.5, 3.0, 0.5, MAX_EXACT_COUNTER.toDouble() + 2.0), events.map { it.value })
        assertEquals(listOf(null, "custom", "millisecond", null, null), events.map { it.unit })
        transaction.finish()
    }
}
