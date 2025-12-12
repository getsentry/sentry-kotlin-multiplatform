package io.sentry.kotlin.multiplatform

/**
 * API for sending structured logs to Sentry.
 *
 * This interface provides methods to log messages at different severity levels.
 * Each platform (JVM, Apple, etc.) provides its own implementation that wraps
 * the native SDK's logging functionality.
 *
 * Usage:
 * ```
 * Sentry.logger().info("A simple log message")
 * Sentry.logger().error("A %s log message", "formatted")
 * Sentry.logger().log(SentryLogLevel.DEBUG, "Log at specific level")
 * ```
 */
public interface SentryLoggerApi {
    /**
     * Logs a message at TRACE level.
     *
     * @param message The message template (supports %s, %d, etc. format specifiers)
     * @param args Arguments to substitute into the message template
     */
    public fun trace(message: String?, vararg args: Any?)

    /**
     * Logs a message at DEBUG level.
     *
     * @param message The message template (supports %s, %d, etc. format specifiers)
     * @param args Arguments to substitute into the message template
     */
    public fun debug(message: String?, vararg args: Any?)

    /**
     * Logs a message at INFO level.
     *
     * @param message The message template (supports %s, %d, etc. format specifiers)
     * @param args Arguments to substitute into the message template
     */
    public fun info(message: String?, vararg args: Any?)

    /**
     * Logs a message at WARN level.
     *
     * @param message The message template (supports %s, %d, etc. format specifiers)
     * @param args Arguments to substitute into the message template
     */
    public fun warn(message: String?, vararg args: Any?)

    /**
     * Logs a message at ERROR level.
     *
     * @param message The message template (supports %s, %d, etc. format specifiers)
     * @param args Arguments to substitute into the message template
     */
    public fun error(message: String?, vararg args: Any?)

    /**
     * Logs a message at FATAL level.
     *
     * @param message The message template (supports %s, %d, etc. format specifiers)
     * @param args Arguments to substitute into the message template
     */
    public fun fatal(message: String?, vararg args: Any?)

    /**
     * Logs a message at the specified level.
     *
     * @param level The log level (TRACE, DEBUG, INFO, WARN, ERROR, FATAL)
     * @param message The message template (supports %s, %d, etc. format specifiers)
     * @param args Arguments to substitute into the message template
     */
    public fun log(level: SentryLogLevel, message: String?, vararg args: Any?)
}

internal expect fun loggerFactory(): SentryLoggerApi