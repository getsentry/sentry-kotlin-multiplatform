package io.sentry.kotlin.multiplatform.log

/**
 * The log level for Sentry structured logs.
 *
 * These levels are used with the [SentryLogger.log] method to specify
 * the severity of log messages.
 */
public enum class SentryLogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL
}
