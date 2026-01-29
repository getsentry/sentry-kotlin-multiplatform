package io.sentry.kotlin.multiplatform.log

/**
 * API for sending structured logs to Sentry.
 *
 * This interface provides methods to log messages at different severity levels.
 * Each platform (JVM, Apple, etc.) provides its own implementation that wraps
 * the native SDK's logging functionality.
 */
public interface SentryLogger {
    /**
     * Logs a message at [SentryLogLevel.TRACE] level.
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     */
    public fun trace(message: String, vararg args: Any?)

    /**
     * Logs a message at [SentryLogLevel.DEBUG] level.
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     */
    public fun debug(message: String, vararg args: Any?)

    /**
     * Logs a message at [SentryLogLevel.INFO] level.
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     */
    public fun info(message: String, vararg args: Any?)

    /**
     * Logs a message at [SentryLogLevel.WARN] level.
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     */
    public fun warn(message: String, vararg args: Any?)

    /**
     * Logs a message at [SentryLogLevel.ERROR] level.
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     */
    public fun error(message: String, vararg args: Any?)

    /**
     * Logs a message at [SentryLogLevel.FATAL] level.
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     */
    public fun fatal(message: String, vararg args: Any?)

    /**
     * Logs a message at [SentryLogLevel.TRACE] level using a DSL builder.
     *
     * Example:
     * ```
     * logger.trace {
     *     message("Executing SQL query: %s", query)
     *     attributes {
     *         this["db.name"] = "users"
     *         this["db.operation"] = "SELECT"
     *     }
     * }
     * ```
     *
     * @param block A lambda with [SentryLogBuilder] receiver to configure the log entry
     */
    public fun trace(block: SentryLogBuilder.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.DEBUG] level using a DSL builder.
     *
     * Example:
     * ```
     * logger.debug {
     *     message("Cache %s for key: %s", if (hit) "hit" else "miss", cacheKey)
     *     attributes {
     *         this["cache.size"] = cache.size
     *         this["cache.ttl"] = ttlSeconds
     *     }
     * }
     * ```
     *
     * @param block A lambda with [SentryLogBuilder] receiver to configure the log entry
     */
    public fun debug(block: SentryLogBuilder.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.INFO] level using a DSL builder.
     *
     * Example:
     * ```
     * logger.info {
     *     message("User %s successfully authenticated", user.email)
     *     attributes {
     *         this["auth.method"] = "oauth2"
     *         this["user.id"] = user.id
     *     }
     * }
     * ```
     *
     * @param block A lambda with [SentryLogBuilder] receiver to configure the log entry
     */
    public fun info(block: SentryLogBuilder.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.WARN] level using a DSL builder.
     *
     * Example:
     * ```
     * logger.warn {
     *     message("API rate limit at %s%% for client %s", usagePercent, clientId)
     *     attributes {
     *         this["rate_limit.current"] = currentRequests
     *         this["rate_limit.max"] = maxRequests
     *         this["rate_limit.reset_at"] = resetTimestamp
     *     }
     * }
     * ```
     *
     * @param block A lambda with [SentryLogBuilder] receiver to configure the log entry
     */
    public fun warn(block: SentryLogBuilder.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.ERROR] level using a DSL builder.
     *
     * Example:
     * ```
     * logger.error {
     *     message("Failed to process payment for order %s: %s", orderId, error.message)
     *     attributes {
     *         this["order.amount"] = amount
     *         this["payment.provider"] = "stripe"
     *         this["error.code"] = error.code
     *     }
     * }
     * ```
     *
     * @param block A lambda with [SentryLogBuilder] receiver to configure the log entry
     */
    public fun error(block: SentryLogBuilder.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.FATAL] level using a DSL builder.
     *
     * Example:
     * ```
     * logger.fatal {
     *     message("Database connection pool exhausted, shutting down service")
     *     attributes {
     *         this["db.host"] = dbHost
     *         this["db.pool.size"] = poolSize
     *         this["db.active_connections"] = activeConnections
     *     }
     * }
     * ```
     *
     * @param block A lambda with [SentryLogBuilder] receiver to configure the log entry
     */
    public fun fatal(block: SentryLogBuilder.() -> Unit)
}
