package io.sentry.kotlin.multiplatform

/** Abstract SentryDate class that describes a date. */
public expect abstract class SentryDate : Comparable<SentryDate> {
  /** Returns the date in nanoseconds as long. */
  public abstract fun nanoTimestamp(): Long

  /**
   * Calculates a date by using another date.
   *
   * This is a workaround for limited precision offered in some cases (e.g. when using [ ]). This
   * makes it possible to have high precision duration by using nanoseconds for the finish timestamp
   * where normally the start and finish timestamps would only offer millisecond precision.
   *
   * @param otherDate another [SentryDate]
   * @return date in seconds as long
   */
  public open fun laterDateNanosTimestampByDiff(otherDate: SentryDate?): Long

  /**
   * Difference between two dates in nanoseconds.
   *
   * @param otherDate another [SentryDate]
   * @return difference in nanoseconds
   */
  public open fun diff(otherDate: SentryDate): Long

  /** Returns true if this date is before the other date. */
  public fun isBefore(otherDate: SentryDate): Boolean

  /** Returns true if this date is after the other date. */
  public fun isAfter(otherDate: SentryDate): Boolean

  /** Returns 1 if this date is after the other date, -1 if it's before, 0 if it's the same. */
  public override fun compareTo(other: SentryDate): Int
}
