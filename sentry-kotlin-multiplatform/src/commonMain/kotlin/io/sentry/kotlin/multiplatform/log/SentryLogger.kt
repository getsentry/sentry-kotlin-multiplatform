package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.SentryAttributes

/**
 * API for sending structured logs to Sentry.
 *
 * This interface provides methods to log messages at different severity levels.
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
     * Logs a message at [SentryLogLevel.TRACE] level with inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.trace("Executing SQL query") {
     *     this["db.name"] = "users"
     *     this["db.operation"] = "SELECT"
     * }
     * ```
     *
     * @param message The log message
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun trace(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.DEBUG] level with inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.debug("Cache lookup for key") {
     *     this["cache.key"] = cacheKey
     *     this["cache.hit"] = true
     * }
     * ```
     *
     * @param message The log message
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun debug(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.INFO] level with inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.info("User authenticated successfully") {
     *     this["auth.method"] = "oauth2"
     *     this["user.id"] = userId
     * }
     * ```
     *
     * @param message The log message
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun info(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.WARN] level with inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.warn("Rate limit reached for endpoint") {
     *     this["endpoint"] = "/api/results/"
     *     this["isEnterprise"] = false
     * }
     * ```
     *
     * @param message The log message
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun warn(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.TRACE] level with template parameters and inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.trace("Executing SQL query: %s", query) {
     *     this["db.name"] = "users"
     *     this["db.operation"] = "SELECT"
     * }
     * ```
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun trace(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.DEBUG] level with template parameters and inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.debug("Cache %s for key: %s", status, cacheKey) {
     *     this["cache.size"] = cache.size
     *     this["cache.ttl"] = ttlSeconds
     * }
     * ```
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun debug(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.INFO] level with template parameters and inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.info("User %s authenticated via %s", userId, method) {
     *     this["auth.method"] = "oauth2"
     *     this["user.id"] = userId
     * }
     * ```
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun info(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.WARN] level with template parameters and inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.warn("Rate limit at %s%% for endpoint %s", usage, endpoint) {
     *     this["endpoint"] = "/api/results/"
     *     this["isEnterprise"] = false
     * }
     * ```
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun warn(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.ERROR] level with template parameters and inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.error("Failed to process order %s: %s", orderId, error) {
     *     this["orderId"] = "order_123"
     *     this["amount"] = 99.99
     * }
     * ```
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun error(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.FATAL] level with template parameters and inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.fatal("Connection pool exhausted on %s", dbHost) {
     *     this["database"] = "users"
     *     this["activeConnections"] = 100
     * }
     * ```
     *
     * @param message The message template (use %s for substitution, %% for literal %)
     * @param args Arguments to substitute into the message template via toString()
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun fatal(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.ERROR] level with inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.error("Failed to process payment") {
     *     this["orderId"] = "order_123"
     *     this["amount"] = 99.99
     * }
     * ```
     *
     * @param message The log message
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun error(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.FATAL] level with inline attributes.
     *
     * Example:
     * ```
     * Sentry.logger.fatal("Database connection pool exhausted") {
     *     this["database"] = "users"
     *     this["activeConnections"] = 100
     * }
     * ```
     *
     * @param message The log message
     * @param attributes A lambda with [SentryAttributes] receiver to set key-value attributes
     */
    public fun fatal(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit)

    /**
     * Logs a message at [SentryLogLevel.TRACE] level using a DSL builder.
     *
     * Example:
     * ```
     * Sentry.logger.trace {
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
     * Sentry.logger.debug {
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
     * Sentry.logger.info {
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
     * Sentry.logger.warn {
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
     * Sentry.logger.error {
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
     * Sentry.logger.fatal {
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
