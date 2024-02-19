package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySpanProtocol
import io.sentry.kotlin.multiplatform.converters.toCocoa
import io.sentry.kotlin.multiplatform.converters.toKmp
import io.sentry.kotlin.multiplatform.protocol.SpanId

internal class SpanAdapter(private val cocoaSpan: SentrySpanProtocol) : Span {
    override fun startChild(operation: String): Span {
        val cocoaSpan = cocoaSpan.startChildWithOperation(operation)
        return SpanAdapter(cocoaSpan)
    }

    override fun startChild(operation: String, description: String?): Span {
        val cocoaSpan =
            cocoaSpan.startChildWithOperation(operation = operation, description = description)
        return SpanAdapter(cocoaSpan)
    }

    override fun finish() {
        cocoaSpan.finish()
    }

    override fun finish(status: SpanStatus) {
        cocoaSpan.finishWithStatus(status.toCocoa())
    }

    override var operation: String
        get() = cocoaSpan.operation
        set(value) {
            cocoaSpan.operation = value
        }

    override var description: String?
        get() = cocoaSpan.spanDescription
        set(value) {
            cocoaSpan.setSpanDescription(value)
        }

    override var status: SpanStatus?
        get() = cocoaSpan.status.toKmp()
        set(value) {
            value?.let {
                cocoaSpan.status = it.toCocoa()
            }
        }

    override val spanId: SpanId = SpanId(cocoaSpan.spanId.toString())

    override val parentSpanId: SpanId? = cocoaSpan.parentSpanId?.let { SpanId(it.toString()) }

    override fun setTag(key: String, value: String) {
        cocoaSpan.setTagValue(value = value, forKey = key)
    }

    override fun getTag(key: String): String? {
        return cocoaSpan.tags[key] as String?
    }

    override val isFinished: Boolean
        get() = cocoaSpan.isFinished

    override fun setData(key: String, value: Any) {
        cocoaSpan.setDataValue(value = value, forKey = key)
    }

    override fun getData(key: String): Any? {
        return cocoaSpan.data[key]
    }
}
