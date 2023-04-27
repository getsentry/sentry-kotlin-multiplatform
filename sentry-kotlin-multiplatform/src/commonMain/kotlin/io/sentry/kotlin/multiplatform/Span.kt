package io.sentry.kotlin.multiplatform

public interface Span {
    /**
     * Starts a child Span.
     *
     * @param operation - new span operation name
     * @return a new transaction span
     */
    public fun startChild(operation: String): Span

    public fun startChild(
        operation: String,
        description: String?,
        timestamp: SentryDate,
        instrumenter: Instrumenter
    ): Span

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

    /**
     * Sets span timestamp marking this span as finished.
     *
     * @param status - the status
     * @param timestamp - the end timestamp
     */
    public fun finish(status: SpanStatus?, timestamp: SentryDate)

    /** The span operation. */
    public var operation: String

    /** The span description. */
    public var description: String?

    /** The span status. */
    public var status: SpanStatus

    /** The throwable that was thrown during the execution of the span. */
    public var throwable: Throwable?

    /**
     * Sets the tag on span or transaction.
     *
     * @param key the tag key
     * @param value the tag value
     */
    public fun setTag(key: String, value: String)
    public fun getTag(key: String): String?

    /**
     * Returns if span has finished.
     *
     * @return if span has finished.
     */
    public val isFinished: Boolean

    /**
     * Sets extra data on span or transaction.
     *
     * @param key the data key
     * @param value the data value
     */
    public fun setData(key: String, value: Any)

    /**
     * Returns extra data from span or transaction.
     *
     * @return the data
     */
    public fun getData(key: String): Any?
}
