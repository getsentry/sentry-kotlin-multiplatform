package io.sentry.kotlin.multiplatform.protocol

/** A log entry message. */
public data class Message(
    /**
     * The log message.
     * It must not exceed 8192 characters. Longer messages will be truncated.
     */
    public var message: String? = null,
    /**
     * Parameters to be interpolated into the log message. This can be an array of positional
     * parameters as well as a mapping of named arguments to their values.
     */
    public var params: List<String>? = null,
    /**
     * The formatted message. If `message` and `params` are given, Sentry will attempt to backfill
     * `formatted` if empty.
     */
    public var formatted: String? = null
)
