@file:OptIn(ExperimentalForeignApi::class)

package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySpanProtocol
import io.sentry.kotlin.multiplatform.extensions.toCocoa
import io.sentry.kotlin.multiplatform.extensions.toKmp
import kotlinx.cinterop.ExperimentalForeignApi

public actual interface ISpan {
    /**
     * Starts a child Span.
     *
     * @param operation - new span operation name
     * @return a new transaction span
     */
    public actual fun startChild(operation: String): ISpan

    /**
     * Starts a child Span.
     *
     * @param operation - new span operation name
     * @param description - new span description name
     * @return a new transaction span
     */
    public actual fun startChild(
        operation: String,
        description: String?
    ): ISpan

    /** Sets span timestamp marking this span as finished.  */
    public actual fun finish()

    /**
     * Sets span timestamp marking this span as finished.
     *
     * @param status - the status
     */
    public actual fun finish(status: SpanStatus2?)

    /**
     * Sets span timestamp marking this span as finished.
     *
     * @param status - the status
     * @param timestamp - the end timestamp
     */
    public actual fun finish(
        status: SpanStatus2?,
        timestamp: SentryDate?
    )

    /**
     * Sets span operation.
     *
     * @param operation - the operation
     */
    public actual fun setOperation(operation: String)

    /**
     * Returns the span operation.
     *
     * @return the operation
     */
    public actual fun getOperation(): String

    /**
     * Sets span description.
     *
     * @param description - the description.
     */
    public actual fun setDescription(description: String?)

    /**
     * Returns the span description.
     *
     * @return the description
     */
    public actual fun getDescription(): String?

    /**
     * Sets span status.
     *
     * @param status - the status.
     */
    public actual fun setStatus(status: SpanStatus2?)

    /**
     * Returns the span status
     *
     * @return the status
     */
    public actual fun getStatus(): SpanStatus2?

    /**
     * Sets extra data on span or transaction.
     *
     * @param key the data key
     * @param value the data value
     */
    public actual fun setData(key: String, value: Any)

    /**
     * Returns extra data from span or transaction.
     *
     * @return the data
     */
    public actual fun getData(key: String): Any?

    /**
     * Returns if span has finished.
     *
     * @return if span has finished.
     */
    public actual fun isFinished(): Boolean
}

public actual class Span actual constructor() : ISpan {
    private var cocoaSpan: SentrySpanProtocol? = null

    public constructor(cocoaSpan: SentrySpanProtocol?) : this() {
        this.cocoaSpan = cocoaSpan
    }

    public actual override fun startChild(operation: String): ISpan {
        val span = cocoaSpan?.startChildWithOperation(operation)
        return Span(cocoaSpan = span)
    }

    public actual override fun startChild(
        operation: String,
        description: String?
    ): ISpan {
        val span = cocoaSpan?.startChildWithOperation(operation, description)
        return Span(cocoaSpan = span)
    }

    public actual override fun finish() {
        cocoaSpan?.finish()
    }

    /**
     * Sets span timestamp marking this span as finished.
     *
     * @param status - the status
     */
    public actual override fun finish(status: SpanStatus2?) {
        status?.toCocoa()?.let { cocoaSpan?.finishWithStatus(it) }
    }

    /**
     * Sets span timestamp marking this span as finished.
     *
     * @param status - the status
     * @param timestamp - the end timestamp
     */
    public actual override fun finish(
        status: SpanStatus2?,
        timestamp: SentryDate?
    ) {
    }

    /**
     * Sets span operation.
     *
     * @param operation - the operation
     */
    public actual override fun setOperation(operation: String) {
        cocoaSpan?.setOperation(operation)
    }

    /**
     * Returns the span operation.
     *
     * @return the operation
     */
    public actual override fun getOperation(): String {
        return cocoaSpan?.operation ?: ""
    }

    /**
     * Sets span description.
     *
     * @param description - the description.
     */
    public actual override fun setDescription(description: String?) {
        cocoaSpan?.setSpanDescription(description)
    }

    /**
     * Returns the span description.
     *
     * @return the description
     */
    public actual override fun getDescription(): String? {
        return cocoaSpan?.spanDescription
    }

    /**
     * Sets span status.
     *
     * @param status - the status.
     */
    public actual override fun setStatus(status: SpanStatus2?) {
        status?.toCocoa()?.let { cocoaSpan?.setStatus(it) }
    }

    /**
     * Returns the span status
     *
     * @return the status
     */
    public actual override fun getStatus(): SpanStatus2? {
        return cocoaSpan?.status?.toKmp()
    }

    /**
     * Sets extra data on span or transaction.
     *
     * @param key the data key
     * @param value the data value
     */
    public actual override fun setData(key: String, value: Any) {
        cocoaSpan?.setDataValue(value, forKey = key)
    }

    /**
     * Returns extra data from span or transaction.
     *
     * @return the data
     */
    public actual override fun getData(key: String): Any? {
        return cocoaSpan?.data?.get(key)
    }

    /**
     * Returns if span has finished.
     *
     * @return if span has finished.
     */
    public actual override fun isFinished(): Boolean {
        return cocoaSpan?.isFinished ?: false
    }
}