package io.sentry.kotlin.multiplatform.protocol

/** The Sentry event ID used to identify an event. */
public expect class SentryId(sentryIdString: String) {
  override fun equals(other: Any?): Boolean

  override fun toString(): String

  override fun hashCode(): Int

  public companion object {
    public val EMPTY_ID: SentryId
  }
}
