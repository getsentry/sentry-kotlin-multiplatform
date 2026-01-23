package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.log.DefaultSentryLogEventBuilder
import io.sentry.kotlin.multiplatform.log.SentryLogEventBuilder
import io.sentry.kotlin.multiplatform.log.SentryLogger

/**
 * Apple platform implementation of [io.sentry.kotlin.multiplatform.log.SentryLogger] that delegates calls
 * to the Cocoa SDK's SentryLogger.
 */
internal class CocoaSentryLoggerDelegate(private val cocoaLogger: CocoaSentryLogger) : SentryLogger {
    override fun trace(message: String, vararg args: Any?) {
        logSimple(message, args) { msg, attrs -> cocoaLogger.trace(msg, attrs) }
    }

    override fun debug(message: String, vararg args: Any?) {
        logSimple(message, args) { msg, attrs -> cocoaLogger.debug(msg, attrs) }
    }

    override fun info(message: String, vararg args: Any?) {
        logSimple(message, args) { msg, attrs -> cocoaLogger.info(msg, attrs) }
    }

    override fun warn(message: String, vararg args: Any?) {
        logSimple(message, args) { msg, attrs -> cocoaLogger.warn(msg, attrs) }
    }

    override fun error(message: String, vararg args: Any?) {
        logSimple(message, args) { msg, attrs -> cocoaLogger.error(msg, attrs) }
    }

    override fun fatal(message: String, vararg args: Any?) {
        logSimple(message, args) { msg, attrs -> cocoaLogger.fatal(msg, attrs) }
    }

    private inline fun logSimple(
        message: String,
        args: Array<out Any?>,
        log: (String, Map<Any?, Any>) -> Unit
    ) {
        val formattedMessage = formatMessage(message, args)
        val attributes = if (args.isEmpty()) emptyMap() else buildTemplateAttributes(message, args)
        log(formattedMessage, attributes)
    }

    override fun trace(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(builder.message, builder.args, builder.attrs) { msg, attrs ->
            cocoaLogger.trace(msg, attrs)
        }
    }

    override fun debug(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(builder.message, builder.args, builder.attrs) { msg, attrs ->
            cocoaLogger.debug(msg, attrs)
        }
    }

    override fun info(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(builder.message, builder.args, builder.attrs) { msg, attrs ->
            cocoaLogger.info(msg, attrs)
        }
    }

    override fun warn(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(builder.message, builder.args, builder.attrs) { msg, attrs ->
            cocoaLogger.warn(msg, attrs)
        }
    }

    override fun error(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(builder.message, builder.args, builder.attrs) { msg, attrs ->
            cocoaLogger.error(msg, attrs)
        }
    }

    override fun fatal(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(builder.message, builder.args, builder.attrs) { msg, attrs ->
            cocoaLogger.fatal(msg, attrs)
        }
    }

    private fun logWithAttributes(
        message: String?,
        args: Array<out Any?>,
        attributes: SentryAttributes,
        log: (String, Map<Any?, Any>) -> Unit
    ) {
        if (message == null) return
        val builtAttributes = buildAttributes(message, args, attributes)
        val formattedMessage = formatMessage(message, args)
        log(formattedMessage, builtAttributes)
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
     * Builds template attributes for structured logging:
     * - sentry.message.template: The original template string
     * - sentry.message.parameter.0, .1, etc.: The parameter values
     */
    private fun buildTemplateAttributes(template: String, args: Array<out Any?>): Map<Any?, Any> {
        val attributes = mutableMapOf<Any?, Any>()
        attributes["sentry.message.template"] = template
        args.forEachIndexed { index, arg ->
            attributes["sentry.message.parameter.$index"] = arg ?: "null"
        }
        return attributes
    }

    /**
     * Builds attributes map including template info and custom attributes from DSL.
     */
    private fun buildAttributes(
        template: String,
        args: Array<out Any?>,
        customAttributes: SentryAttributes
    ): Map<Any?, Any> {
        // Fast path: no args and no custom attributes
        if (args.isEmpty() && customAttributes.isEmpty()) {
            return emptyMap()
        }

        val attributes = mutableMapOf<Any?, Any>()

        // Add template info if there are args
        if (args.isNotEmpty()) {
            attributes["sentry.message.template"] = template
            args.forEachIndexed { index, arg ->
                attributes["sentry.message.parameter.$index"] = arg ?: "null"
            }
        }

        // Add custom attributes from DSL
        customAttributes.forEach { attribute ->
            attributes[attribute.key] = attribute.value
        }

        return attributes
    }
}