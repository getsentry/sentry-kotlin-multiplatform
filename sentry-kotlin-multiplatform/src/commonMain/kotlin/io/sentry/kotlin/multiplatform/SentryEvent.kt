package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.protocol.Message
import io.sentry.kotlin.multiplatform.protocol.SentryException

/** Represents an event that is sent to Sentry. */
public expect class SentryEvent() : SentryBaseEvent {
  /** The event message. */
  public var message: Message?

  /** Logger that created the event. */
  public var logger: String?

  /** Severity level of the event. */
  public var level: SentryLevel?

  /**
   * Manual fingerprint override.
   *
   * A list of strings used to dictate how this event is supposed to be grouped with other events
   * into issues. For more information about overriding grouping see
   * [Customize Grouping with Fingerprints](https://docs.sentry.io/data-management/event-grouping/).
   *
   * This is not thread-safe.
   */
  public var fingerprint: MutableList<String>

  /**
   * One or multiple chained (nested) exceptions.
   *
   * This is not thread-safe.
   */
  public var exceptions: MutableList<SentryException>
}
