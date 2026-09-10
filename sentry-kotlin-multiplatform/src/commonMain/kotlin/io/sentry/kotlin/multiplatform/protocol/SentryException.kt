package io.sentry.kotlin.multiplatform.protocol

/**
 * A single exception.
 *
 * Multiple values inside of a [io.sentry.kotlin.multiplatform.SentryEvent] represent chained
 * exceptions and should be sorted oldest to newest.
 */
public data class SentryException(
    /** The exception type. */
    val type: String? = null,
    /** Human readable display value. */
    val value: String? = null,
    /** The optional module, or package which the exception type lives in. */
    val module: String? = null,
    /** An optional value that refers to a thread. */
    val threadId: Long? = null
)
