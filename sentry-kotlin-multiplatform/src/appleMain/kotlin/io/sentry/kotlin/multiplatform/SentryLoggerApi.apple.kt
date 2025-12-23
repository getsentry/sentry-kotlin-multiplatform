package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.log.SentryLogLevel
import io.sentry.kotlin.multiplatform.log.SentryLoggerApi

/**
 * Apple platform implementation of [io.sentry.kotlin.multiplatform.log.SentryLoggerApi] that delegates calls
 * to the Cocoa SDK's SentryLogger.
 */
internal class CocoaSentryLoggerDelegate(private val cocoaLogger: CocoaSentryLogger) : SentryLoggerApi {
    override fun trace(message: String?, vararg args: Any?) {
        if (message == null) return
        val attributes = buildAttributes(message, args)
        val formattedMessage = formatMessage(message, args)
        cocoaLogger.trace(formattedMessage, attributes)
    }

    override fun debug(message: String?, vararg args: Any?) {
        if (message == null) return
        val attributes = buildAttributes(message, args)
        val formattedMessage = formatMessage(message, args)
        cocoaLogger.debug(formattedMessage, attributes)
    }

    override fun info(message: String?, vararg args: Any?) {
        if (message == null) return
        val attributes = buildAttributes(message, args)
        val formattedMessage = formatMessage(message, args)
        cocoaLogger.info(formattedMessage, attributes)
    }

    override fun warn(message: String?, vararg args: Any?) {
        if (message == null) return
        val attributes = buildAttributes(message, args)
        val formattedMessage = formatMessage(message, args)
        cocoaLogger.warn(formattedMessage, attributes)
    }

    override fun error(message: String?, vararg args: Any?) {
        if (message == null) return
        val attributes = buildAttributes(message, args)
        val formattedMessage = formatMessage(message, args)
        cocoaLogger.error(formattedMessage, attributes)
    }

    override fun fatal(message: String?, vararg args: Any?) {
        if (message == null) return
        val attributes = buildAttributes(message, args)
        val formattedMessage = formatMessage(message, args)
        cocoaLogger.fatal(formattedMessage, attributes)
    }

    override fun log(level: SentryLogLevel, message: String?, vararg args: Any?) {
        when (level) {
            SentryLogLevel.TRACE -> trace(message, *args)
            SentryLogLevel.DEBUG -> debug(message, *args)
            SentryLogLevel.INFO -> info(message, *args)
            SentryLogLevel.WARN -> warn(message, *args)
            SentryLogLevel.ERROR -> error(message, *args)
            SentryLogLevel.FATAL -> fatal(message, *args)
        }
    }

    /**
     * Formats the message using Java-style format specifiers (%s, %d, etc.)
     * This mirrors the behavior of Java's String.format
     */
    private fun formatMessage(template: String, args: Array<out Any?>): String {
        if (args.isEmpty()) return template

        var result = template
        var argIndex = 0

        // Simple regex to match format specifiers like %s, %d, %f, etc.
        val formatPattern = Regex("%[sdfiboxXeEgGaAcCbBhH%]")

        result = formatPattern.replace(result) { matchResult ->
            when (matchResult.value) {
                "%%" -> "%" // Escaped percent sign
                else -> {
                    if (argIndex < args.size) {
                        val arg = args[argIndex++]
                        arg?.toString() ?: "null"
                    } else {
                        matchResult.value // Keep the placeholder if no arg available
                    }
                }
            }
        }

        return result
    }

    /**
     * Builds the attributes map:
     * - sentry.message.template: The original template string
     * - sentry.message.parameter.0, .1, etc.: The parameter values
     */
    private fun buildAttributes(template: String, args: Array<out Any?>): Map<Any?, Any> {
        if (args.isEmpty()) return emptyMap()

        val attributes = mutableMapOf<Any?, Any>()
        attributes["sentry.message.template"] = template

        args.forEachIndexed { index, arg ->
            val value = arg ?: "null"
            attributes["sentry.message.parameter.$index"] = value
        }

        return attributes
    }
}