package io.sentry.kotlin.multiplatform.log

/**
 * The log level for Sentry structured logs.
 */
public enum class SentryLogLevel {
    /** Fine-grained details for tracing execution. */
    TRACE,

    /** Diagnostic information for debugging. */
    DEBUG,

    /** Information about normal application activity. */
    INFO,

    /** Potential problems that do not prevent continued operation. */
    WARN,

    /** Failures that prevent an operation from completing. */
    ERROR,

    /** Critical failures that make the application unusable. */
    FATAL
}
