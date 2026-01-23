package io.sentry.kotlin.multiplatform.log

import io.sentry.logger.ILoggerApi
import io.sentry.logger.SentryLogParameters

/**
 * Adapter that bridges KMP [SentryLogger] to the Java SDK's [ILoggerApi].
 *
 * Extends [BaseSentryLogger] which handles all builder logic. This class only
 * implements [sendLog] to adapt and forward formatted logs to the Java SDK.
 */
internal class JvmSentryLoggerAdapter(
    private val jvmLogger: ILoggerApi,
    logBuilderFactory: SentryLogBuilderFactory = DefaultSentryLogBuilderFactory
) : BaseSentryLogger(logBuilderFactory) {
    override fun sendLog(level: SentryLogLevel, formatted: FormattedLog) {
        val jvmLevel = level.toJvmSentryLogLevel()
        if (formatted.attributes.isEmpty()) {
            jvmLogger.log(jvmLevel, formatted.body)
        } else {
            val params = SentryLogParameters.create(formatted.attributes.toJvmSentryAttributes())
            jvmLogger.log(jvmLevel, params, formatted.body)
        }
    }
}
