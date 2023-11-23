package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SpanId

/** Represents a performance monitoring Span. */
public interface Span {
    /**
     * Starts a child Span.
     *
     * @param operation - new span operation name
     * @return a new transaction span
     */
    public fun startChild(operation: String): Span

    /**
     * Starts a child Span.
     *
     * @param operation - new span operation name
     * @param description - new span description name
     * @return a new transaction span
     */
    public fun startChild(operation: String, description: String?): Span

    /** Sets span timestamp marking this span as finished.  */
    public fun finish()

    /**
     * Sets span timestamp marking this span as finished.
     *
     * @param status - the status
     */
    public fun finish(status: SpanStatus)

    /** The span operation. */
    public var operation: String

    /** The span description. */
    public var description: String?

    /** The span status. */
    public var status: SpanStatus?

    /** The span spanId. */
    public val spanId: SpanId

    /** The span parentSpanId. */
    public val parentSpanId: SpanId?

    /**
     * Sets the tag on span.
     *
     * @param key the tag key
     * @param value the tag value
     */
    public fun setTag(key: String, value: String)

    /**
     * Returns the tag value from span.
     *
     * @return the tag value
     */
    public fun getTag(key: String): String?

    /**
     * Returns if span has finished.
     *
     * @return if span has finished.
     */
    public val isFinished: Boolean

    /**
     * Sets extra data on span.
     *
     * @param key the data key
     * @param value the data value
     */
    public fun setData(key: String, value: Any)

    /**
     * Returns extra data from span.
     *
     * @return the data
     */
    public fun getData(key: String): Any?
}