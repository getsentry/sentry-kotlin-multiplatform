package io.sentry.kotlin.multiplatform

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
  public actual fun startChild(operation: String, description: String?): ISpan

  /** Sets span timestamp marking this span as finished. */
  public actual fun finish()

  /**
   * Sets span timestamp marking this span as finished.
   *
   * @param status - the status
   */
  public actual fun finish(status: SpanStatus?)

  /**
   * Sets span timestamp marking this span as finished.
   *
   * @param status - the status
   * @param timestamp - the end timestamp
   */
  public actual fun finish(status: SpanStatus?, timestamp: SentryDate?)

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
  public actual fun setStatus(status: SpanStatus?)

  /**
   * Returns the span status
   *
   * @return the status
   */
  public actual fun getStatus(): SpanStatus?

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

  /**
   * Returns the trace information that could be sent as a sentry-trace header.
   *
   * @return SentryTraceHeader.
   */
  public actual fun toSentryTrace(): SentryTraceHeader
}
