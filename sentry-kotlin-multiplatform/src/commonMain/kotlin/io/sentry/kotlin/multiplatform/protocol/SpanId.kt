package io.sentry.kotlin.multiplatform.protocol

/** The Sentry span ID used to identify a [io.sentry.kotlin.multiplatform.ISpan]. */
public expect class SpanId(spanIdString: String) {
  override fun equals(other: Any?): Boolean

  override fun toString(): String

  override fun hashCode(): Int

  public companion object {
    /** Creates a new empty [SpanId]. */
    public val EMPTY_ID: SpanId
  }
}
