package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.SentryAttributes
import io.sentry.kotlin.multiplatform.CocoaSentryLogger

/**
 * Adapter that bridges KMP [SentryLogger] to the Cocoa SDK's SentryLogger.
 *
 * Extends [BaseSentryLogger] which handles all builder logic. This class only
 * implements [sendLog] to adapt and forward formatted logs to the Cocoa SDK.
 */
internal class CocoaSentryLoggerAdapter(
    private val cocoaLogger: CocoaSentryLogger,
    logBuilderFactory: SentryLogBuilderFactory = DefaultSentryLogBuilderFactory
) : BaseSentryLogger(logBuilderFactory) {
    override fun sendLog(level: SentryLogLevel, formatted: FormattedLog) {
        val attributes = formatted.attributes.toCocoaMap()
        when (level) {
            SentryLogLevel.TRACE -> cocoaLogger.trace(formatted.body, attributes)
            SentryLogLevel.DEBUG -> cocoaLogger.debug(formatted.body, attributes)
            SentryLogLevel.INFO -> cocoaLogger.info(formatted.body, attributes)
            SentryLogLevel.WARN -> cocoaLogger.warn(formatted.body, attributes)
            SentryLogLevel.ERROR -> cocoaLogger.error(formatted.body, attributes)
            SentryLogLevel.FATAL -> cocoaLogger.fatal(formatted.body, attributes)
        }
    }
}

/**
 * Converts [SentryAttributes] to a Cocoa-compatible map.
 */
internal fun SentryAttributes.toCocoaMap(): Map<Any?, Any> {
    if (isEmpty()) return emptyMap()
    val result = mutableMapOf<Any?, Any>()
    forEach { (key, attrValue) ->
        result[key] = attrValue.value
    }
    return result
}