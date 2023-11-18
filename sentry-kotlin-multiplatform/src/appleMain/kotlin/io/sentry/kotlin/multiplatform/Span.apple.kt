package io.sentry.kotlin.multiplatform

import cocoapods.Sentry.SentrySDK
import cocoapods.Sentry.SentrySpanProtocol
import io.sentry.kotlin.multiplatform.extensions.toCocoa
import io.sentry.kotlin.multiplatform.extensions.toKmp
import io.sentry.kotlin.multiplatform.protocol.SentryId
import io.sentry.kotlin.multiplatform.protocol.SpanId

public actual class Span private actual constructor() : ISpan {
  private lateinit var cocoaSpan: SentrySpanProtocol

  public constructor(cocoaSpan: SentrySpanProtocol) : this() {
    this.cocoaSpan = cocoaSpan
  }

  public actual override fun startChild(operation: String): ISpan {
    val span = cocoaSpan.startChildWithOperation(operation)
    return Span(cocoaSpan = span)
  }

  public actual override fun startChild(operation: String, description: String?): ISpan {
    val span = cocoaSpan.startChildWithOperation(operation, description)
    return Span(cocoaSpan = span)
  }

  public actual override fun finish() {
    cocoaSpan.finish()
  }

  /**
   * Sets span timestamp marking this span as finished.
   *
   * @param status - the status
   */
  public actual override fun finish(status: SpanStatus?) {
    status?.toCocoa()?.let { cocoaSpan.finishWithStatus(it) }
  }

  /**
   * Sets span timestamp marking this span as finished.
   *
   * @param status - the status
   * @param timestamp - the end timestamp
   */
  public actual override fun finish(status: SpanStatus?, timestamp: SentryDate?) {}

  /**
   * Sets span operation.
   *
   * @param operation - the operation
   */
  public actual override fun setOperation(operation: String) {
    cocoaSpan.setOperation(operation)
  }

  /**
   * Returns the span operation.
   *
   * @return the operation
   */
  public actual override fun getOperation(): String {
    return cocoaSpan.operation
  }

  /**
   * Sets span description.
   *
   * @param description - the description.
   */
  public actual override fun setDescription(description: String?) {
    cocoaSpan.setSpanDescription(description)
  }

  /**
   * Returns the span description.
   *
   * @return the description
   */
  public actual override fun getDescription(): String? {
    return cocoaSpan.spanDescription
  }

  /**
   * Sets span status.
   *
   * @param status - the status.
   */
  public actual override fun setStatus(status: SpanStatus?) {
    status?.toCocoa()?.let { cocoaSpan.setStatus(it) }
  }

  /**
   * Returns the span status
   *
   * @return the status
   */
  public actual override fun getStatus(): SpanStatus? {
    return cocoaSpan.status.toKmp()
  }

  /**
   * Sets extra data on span or transaction.
   *
   * @param key the data key
   * @param value the data value
   */
  public actual override fun setData(key: String, value: Any) {
    cocoaSpan.setDataValue(value, forKey = key)
  }

  /**
   * Returns extra data from span or transaction.
   *
   * @return the data
   */
  public actual override fun getData(key: String): Any? {
    return cocoaSpan.data?.get(key)
  }

  /**
   * Returns if span has finished.
   *
   * @return if span has finished.
   */
  public actual override fun isFinished(): Boolean {
    return cocoaSpan.isFinished
  }

  override fun toSentryTrace(): SentryTraceHeader {
    return SentryTraceHeader(cocoaSpan.toTraceHeader())
  }

  public actual fun getTraceId(): SentryId {
    return SentryId(cocoaSpan.traceId.sentryIdString)
  }

  public actual fun getSpanId(): SpanId {
    return SpanId(cocoaSpan.spanId.sentrySpanIdString)
  }

  public actual fun getParentSpanId(): SpanId? {
    return cocoaSpan.parentSpanId?.let { SpanId(it.sentrySpanIdString) }
  }
}
