package io.sentry.kotlin.multiplatform.log

/**
 * Options for Sentry structured logs.
 */
public class SentryLogOptions {
    /** Whether logs feature is enabled. */
    public var enabled: Boolean = false

    /**
     * A callback that is invoked before a log is sent to Sentry.
     *
     * Return the (potentially modified) log to send it, or null to drop it.
     */
    public var beforeSend: ((SentryLog) -> SentryLog?)? = null
}
