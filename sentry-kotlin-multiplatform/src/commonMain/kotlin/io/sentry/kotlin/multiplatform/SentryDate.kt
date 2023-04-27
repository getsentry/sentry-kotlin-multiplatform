package io.sentry.kotlin.multiplatform

public abstract class SentryDate : Comparable<SentryDate?> {
    /** Returns the date in nanoseconds as long.  */
    public abstract fun nanoTimestamp(): Long

    /**
     * Calculates a date by using another date.
     *
     *
     * This is a workaround for limited precision offered in some cases (e.g. when using [ ]). This makes it possible to have high precision duration by using
     * nanoseconds for the finish timestamp where normally the start and finish timestamps would only
     * offer millisecond precision.
     *
     * @param otherDate another [SentryDate]
     * @return date in seconds as long
     */
    public fun laterDateNanosTimestampByDiff(otherDate: SentryDate?): Long {
        return if (otherDate != null && compareTo(otherDate) < 0) {
            otherDate.nanoTimestamp()
        } else {
            nanoTimestamp()
        }
    }

    /**
     * Difference between two dates in nanoseconds.
     *
     * @param otherDate another [SentryDate]
     * @return difference in nanoseconds
     */
    public fun diff(otherDate: SentryDate): Long {
        return nanoTimestamp() - otherDate.nanoTimestamp()
    }

    public override fun compareTo(other: SentryDate?): Int {
        return nanoTimestamp().compareTo(other?.nanoTimestamp() ?: 0)
    }
}
