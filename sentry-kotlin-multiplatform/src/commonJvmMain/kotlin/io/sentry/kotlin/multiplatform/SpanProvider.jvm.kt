package io.sentry.kotlin.multiplatform

import io.sentry.ISpan
import io.sentry.kotlin.multiplatform.extensions.toJvm
import io.sentry.kotlin.multiplatform.extensions.toKmp
import io.sentry.kotlin.multiplatform.protocol.SpanId

internal class SpanProvider(private val jvmSpan: ISpan) : Span {
    override fun startChild(operation: String): Span {
        val jvmSpan = jvmSpan.startChild(operation)
        return SpanProvider(jvmSpan)
    }

    override fun startChild(operation: String, description: String?): Span {
        val jvmSpan = jvmSpan.startChild(operation, description)
        return SpanProvider(jvmSpan)
    }

    override fun finish() {
        jvmSpan.finish()
    }

    override fun finish(status: SpanStatus) {
        jvmSpan.finish(status.toJvm())
    }

    override var operation: String
        get() = jvmSpan.operation
        set(value) {
            jvmSpan.operation = value
        }
    override var description: String?
        get() = jvmSpan.description
        set(value) {
            jvmSpan.description = value
        }
    override var status: SpanStatus?
        get() = jvmSpan.status?.toKmp()
        set(value) {
            jvmSpan.status = value?.toJvm()
        }

    override val spanId: SpanId = SpanId(jvmSpan.spanContext.spanId.toString())

    override val parentSpanId: SpanId? =
        jvmSpan.spanContext.parentSpanId?.let { SpanId(it.toString()) }

    override fun setTag(key: String, value: String) {
        jvmSpan.setTag(key, value)
    }

    override fun getTag(key: String): String? {
        return jvmSpan.getTag(key)
    }

    override val isFinished: Boolean
        get() = jvmSpan.isFinished

    override fun setData(key: String, value: Any) {
        jvmSpan.setData(key, value)
    }

    override fun getData(key: String): Any? {
        return jvmSpan.getData(key)
    }
}