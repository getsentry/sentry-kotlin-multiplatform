package io.sentry.kotlin.multiplatform

import io.sentry.SentryLogEventAttributeValue
import io.sentry.SentryOptions
import io.sentry.logger.ILoggerApi
import io.sentry.kotlin.multiplatform.extensions.toJvmSentryLogLevel
import io.sentry.kotlin.multiplatform.log.SentryLogLevel
import io.sentry.kotlin.multiplatform.log.SentryLoggerApi

/**
 * JVM implementation of [io.sentry.kotlin.multiplatform.log.SentryLoggerApi] that wraps the Java SDK's [ILoggerApi].
 *
 * This is a thin wrapper that delegates all calls to the underlying Java logger,
 * converting KMP types to Java types as needed.
 */
internal class JvmSentryLogger(private val jvmLogger: ILoggerApi) : SentryLoggerApi {
    override fun trace(message: String?, vararg args: Any?) {
        jvmLogger.trace(message, *args)
    }

    override fun debug(message: String?, vararg args: Any?) {
        jvmLogger.debug(message, *args)
    }

    override fun info(message: String?, vararg args: Any?) {
        jvmLogger.info(message, *args)
    }

    override fun warn(message: String?, vararg args: Any?) {
        jvmLogger.warn(message, *args)
    }

    override fun error(message: String?, vararg args: Any?) {
        jvmLogger.error(message, *args)
    }

    override fun fatal(message: String?, vararg args: Any?) {
        jvmLogger.fatal(message, *args)
    }

    override fun log(level: SentryLogLevel, message: String?, vararg args: Any?) {
        jvmLogger.log(level.toJvmSentryLogLevel(), message, *args)
    }
}
