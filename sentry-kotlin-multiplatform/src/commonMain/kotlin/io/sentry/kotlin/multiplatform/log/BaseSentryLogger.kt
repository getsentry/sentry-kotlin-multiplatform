package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.SentryAttributes

/**
 * Base implementation of [SentryLogger] that handles builder logic.
 *
 * This class provides the common implementation for all platforms:
 * - Creates and configures the [SentryLogBuilder]
 * - Calls [SentryLogBuilder.buildFormatted] to get the formatted log
 * - Delegates the final send to [sendLog] which platforms implement
 *
 * Platform implementations only need to override [sendLog] to send
 * the formatted log to their native SDK.
 */
internal abstract class BaseSentryLogger(
    private val logBuilderFactory: SentryLogBuilderFactory
) : SentryLogger {
    /**
     * Sends a formatted log to the native SDK.
     * Platforms implement this to handle the final delivery.
     *
     * @param level The log level
     * @param formatted The formatted log with body and attributes
     */
    protected abstract fun sendLog(level: SentryLogLevel, formatted: FormattedLog)

    override fun trace(message: String, vararg args: Any?) =
        log(SentryLogLevel.TRACE, message, args)
    override fun debug(message: String, vararg args: Any?) =
        log(SentryLogLevel.DEBUG, message, args)
    override fun info(message: String, vararg args: Any?) =
        log(SentryLogLevel.INFO, message, args)
    override fun warn(message: String, vararg args: Any?) =
        log(SentryLogLevel.WARN, message, args)
    override fun error(message: String, vararg args: Any?) =
        log(SentryLogLevel.ERROR, message, args)
    override fun fatal(message: String, vararg args: Any?) =
        log(SentryLogLevel.FATAL, message, args)

    override fun trace(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.TRACE, message, attributes = attributes)
    override fun debug(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.DEBUG, message, attributes = attributes)
    override fun info(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.INFO, message, attributes = attributes)
    override fun warn(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.WARN, message, attributes = attributes)
    override fun error(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.ERROR, message, attributes = attributes)
    override fun fatal(message: String, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.FATAL, message, attributes = attributes)

    override fun trace(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.TRACE, message, args, attributes)
    override fun debug(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.DEBUG, message, args, attributes)
    override fun info(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.INFO, message, args, attributes)
    override fun warn(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.WARN, message, args, attributes)
    override fun error(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.ERROR, message, args, attributes)
    override fun fatal(message: String, vararg args: Any?, attributes: @SentryLogDsl SentryAttributes.() -> Unit) =
        log(SentryLogLevel.FATAL, message, args, attributes)

    override fun trace(block: SentryLogBuilder.() -> Unit) =
        log(block, SentryLogLevel.TRACE)
    override fun debug(block: SentryLogBuilder.() -> Unit) =
        log(block, SentryLogLevel.DEBUG)
    override fun info(block: SentryLogBuilder.() -> Unit) =
        log(block, SentryLogLevel.INFO)
    override fun warn(block: SentryLogBuilder.() -> Unit) =
        log(block, SentryLogLevel.WARN)
    override fun error(block: SentryLogBuilder.() -> Unit) =
        log(block, SentryLogLevel.ERROR)
    override fun fatal(block: SentryLogBuilder.() -> Unit) =
        log(block, SentryLogLevel.FATAL)

    @Suppress("SpreadOperator")
    private fun log(
        level: SentryLogLevel,
        message: String,
        args: Array<out Any?> = emptyArray(),
        attributes: (@SentryLogDsl SentryAttributes.() -> Unit)? = null
    ) = log({
        if (args.isEmpty()) message(message) else message(message, *args)
        attributes?.let { attributes(it) }
    }, level)

    private inline fun log(block: SentryLogBuilder.() -> Unit, level: SentryLogLevel) {
        val formatted = logBuilderFactory().apply(block).buildFormatted() ?: return
        sendLog(level, formatted)
    }
}
