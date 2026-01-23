package io.sentry.kotlin.multiplatform

import io.sentry.logger.ILoggerApi
import io.sentry.logger.SentryLogParameters
import io.sentry.kotlin.multiplatform.extensions.toJvmSentryAttributes
import io.sentry.kotlin.multiplatform.log.DefaultSentryLogEventBuilder
import io.sentry.kotlin.multiplatform.log.SentryLogEventBuilder
import io.sentry.kotlin.multiplatform.log.SentryLogger

/**
 * JVM implementation of [io.sentry.kotlin.multiplatform.log.SentryLogger] that wraps the Java SDK's [ILoggerApi].
 *
 * This is a thin wrapper that delegates all calls to the underlying Java logger,
 * converting KMP types to Java types as needed.
 */
internal class JvmSentryLogger(private val jvmLogger: ILoggerApi) : SentryLogger {
    override fun trace(message: String, vararg args: Any?) {
        jvmLogger.trace(message, *args)
    }

    override fun debug(message: String, vararg args: Any?) {
        jvmLogger.debug(message, *args)
    }

    override fun info(message: String, vararg args: Any?) {
        jvmLogger.info(message, *args)
    }

    override fun warn(message: String, vararg args: Any?) {
        jvmLogger.warn(message, *args)
    }

    override fun error(message: String, vararg args: Any?) {
        jvmLogger.error(message, *args)
    }

    override fun fatal(message: String, vararg args: Any?) {
        jvmLogger.fatal(message, *args)
    }

    override fun trace(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(JvmSentryLogLevel.TRACE, builder.message, builder.args, builder.attrs)
    }

    override fun debug(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(JvmSentryLogLevel.DEBUG, builder.message, builder.args, builder.attrs)
    }

    override fun info(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(JvmSentryLogLevel.INFO, builder.message, builder.args, builder.attrs)
    }

    override fun warn(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(JvmSentryLogLevel.WARN, builder.message, builder.args, builder.attrs)
    }

    override fun error(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(JvmSentryLogLevel.ERROR, builder.message, builder.args, builder.attrs)
    }

    override fun fatal(block: SentryLogEventBuilder.() -> Unit) {
        val builder = DefaultSentryLogEventBuilder().apply(block)
        logWithAttributes(JvmSentryLogLevel.FATAL, builder.message, builder.args, builder.attrs)
    }

    private fun logWithAttributes(
        level: JvmSentryLogLevel,
        message: String?,
        args: Array<out Any?>,
        attributes: SentryAttributes
    ) {
        if (attributes.isEmpty()) {
            jvmLogger.log(level, message, *args)
        } else {
            val params = SentryLogParameters.create(attributes.toJvmSentryAttributes())
            jvmLogger.log(level, params, message, *args)
        }
    }
}
