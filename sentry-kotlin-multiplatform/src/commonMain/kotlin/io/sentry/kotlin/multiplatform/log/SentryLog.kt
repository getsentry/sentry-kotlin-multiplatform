package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.SentryAttributes

/**
 * Represents a structured log entry.
 *
 * This is used to be able to modify logs within [SentryLogOptions.beforeSend].
 */
public open class SentryLog(
    /** Unix timestamp in seconds when the log was created. */
    public open val timestamp: Double,

    /** The severity level of this log entry. */
    public open var level: SentryLogLevel,

    /** The log message body. */
    public open var body: String,

    /** Optional numeric severity. */
    public open var severityNumber: Int? = null,

    /** Custom key-value attributes attached to this log entry. */
    public open val attributes: SentryAttributes = SentryAttributes.empty()
)
