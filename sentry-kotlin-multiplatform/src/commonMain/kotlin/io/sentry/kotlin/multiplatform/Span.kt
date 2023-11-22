package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId

/** The Span interface - represents a span of timed activity within the application. */
public expect class Span private constructor() : ISpan {
  public fun getTraceId(): SentryId

  public fun getSpanId(): SpanId

  public fun getParentSpanId(): SpanId?

  /**
   * Starts a child Span.
   *
   * @param operation - new span operation name
   * @return a new transaction span
   */
  public override fun startChild(operation: String): ISpan

  /**
   * Starts a child Span.
   *
   * @param operation - new span operation name
   * @param description - new span description name
   * @return a new transaction span
   */
  public override fun startChild(operation: String, description: String?): ISpan

  /** Sets span timestamp marking this span as finished. */
  public override fun finish()

  /**
   * Sets span timestamp marking this span as finished.
   *
   * @param status - the status
   */
  public override fun finish(status: SpanStatus?)

  /**
   * Sets span operation.
   *
   * @param operation - the operation
   */
  public override fun setOperation(operation: String)

  /**
   * Returns the span operation.
   *
   * @return the operation
   */
  public override fun getOperation(): String

  /**
   * Sets span description.
   *
   * @param description - the description.
   */
  public override fun setDescription(description: String?)

  /**
   * Returns the span description.
   *
   * @return the description
   */
  public override fun getDescription(): String?

  /**
   * Sets span status.
   *
   * @param status - the status.
   */
  public override fun setStatus(status: SpanStatus?)

  /**
   * Returns the span status
   *
   * @return the status
   */
  public override fun getStatus(): SpanStatus?

  /**
   * Sets extra data on span or transaction.
   *
   * @param key the data key
   * @param value the data value
   */
  public override fun setData(key: String, value: Any)

  /**
   * Returns extra data from span or transaction.
   *
   * @return the data
   */
  public override fun getData(key: String): Any?

  /**
   * Returns if span has finished.
   *
   * @return if span has finished.
   */
  public override fun isFinished(): Boolean
}
