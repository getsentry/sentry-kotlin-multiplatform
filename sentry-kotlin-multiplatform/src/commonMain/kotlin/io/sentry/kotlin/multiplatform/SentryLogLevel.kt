package io.sentry.kotlin.multiplatform

/**
 * The log level for Sentry structured logs.
 *
 * These levels are used with the [SentryLoggerApi.log] method to specify
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
