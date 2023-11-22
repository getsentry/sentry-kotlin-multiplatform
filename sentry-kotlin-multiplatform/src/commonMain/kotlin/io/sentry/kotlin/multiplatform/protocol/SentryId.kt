package io.sentry.kotlin.multiplatform.protocol

/** The Sentry event ID used to identify a [io.sentry.kotlin.multiplatform.SentryEvent]. */
public expect class SentryId(sentryIdString: String) {
  override fun equals(other: Any?): Boolean

  override fun toString(): String

  override fun hashCode(): Int

  public companion object {
    /** Creates a new empty [SentryId]. */
    public val EMPTY_ID: SentryId
  }
}
