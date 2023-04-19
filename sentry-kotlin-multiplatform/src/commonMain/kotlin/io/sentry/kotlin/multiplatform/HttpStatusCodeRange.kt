package io.sentry.kotlin.multiplatform

/**
 * The Http status code range. Example for a range: 400 to 499, 500 to 599, 400 to 599 The range is
 * inclusive so the min and max is considered part of the range.
 *
 * Example for a single status code 400, 500
 */
public data class HttpStatusCodeRange(val min: Int = DEFAULT_MIN, val max: Int = DEFAULT_MAX) {

    public constructor(statusCode: Int) : this(statusCode, statusCode)

    public fun isInRange(statusCode: Int): Boolean {
        return statusCode in min..max
    }

    public companion object {
        public const val DEFAULT_MIN: Int = 500
        public const val DEFAULT_MAX: Int = 599
    }
}
