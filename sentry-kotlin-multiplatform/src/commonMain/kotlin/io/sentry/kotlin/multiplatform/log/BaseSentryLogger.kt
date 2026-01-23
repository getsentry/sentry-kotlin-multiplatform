package io.sentry.kotlin.multiplatform.log

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
        logSimple(message, args, SentryLogLevel.TRACE)
    override fun debug(message: String, vararg args: Any?) =
        logSimple(message, args, SentryLogLevel.DEBUG)
    override fun info(message: String, vararg args: Any?) =
        logSimple(message, args, SentryLogLevel.INFO)
    override fun warn(message: String, vararg args: Any?) =
        logSimple(message, args, SentryLogLevel.WARN)
    override fun error(message: String, vararg args: Any?) =
        logSimple(message, args, SentryLogLevel.ERROR)
    override fun fatal(message: String, vararg args: Any?) =
        logSimple(message, args, SentryLogLevel.FATAL)

    override fun trace(block: SentryLogBuilder.() -> Unit) =
        logWithBuilder(block, SentryLogLevel.TRACE)
    override fun debug(block: SentryLogBuilder.() -> Unit) =
        logWithBuilder(block, SentryLogLevel.DEBUG)
    override fun info(block: SentryLogBuilder.() -> Unit) =
        logWithBuilder(block, SentryLogLevel.INFO)
    override fun warn(block: SentryLogBuilder.() -> Unit) =
        logWithBuilder(block, SentryLogLevel.WARN)
    override fun error(block: SentryLogBuilder.() -> Unit) =
        logWithBuilder(block, SentryLogLevel.ERROR)
    override fun fatal(block: SentryLogBuilder.() -> Unit) =
        logWithBuilder(block, SentryLogLevel.FATAL)

    private fun logSimple(message: String, args: Array<out Any?>, level: SentryLogLevel) =
        logWithBuilder({
            if (args.isEmpty()) message(message) else message(message, *args)
        }, level)

    private inline fun logWithBuilder(block: SentryLogBuilder.() -> Unit, level: SentryLogLevel) {
        val formatted = logBuilderFactory().apply(block).buildFormatted() ?: return
        sendLog(level, formatted)
    }
}
