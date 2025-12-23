package io.sentry.kotlin.multiplatform.extensions

import cocoapods.Sentry.SentryLogLevel as CocoaSentryLogLevel
import cocoapods.Sentry.SentryLog as CocoaSentryLog
import io.sentry.kotlin.multiplatform.SentryAttributes as KmpSentryAttributes
import io.sentry.kotlin.multiplatform.SentryAttribute as KmpSentryAttribute
import io.sentry.kotlin.multiplatform.log.SentryLog
import io.sentry.kotlin.multiplatform.log.SentryLogLevel
import io.sentry.kotlin.multiplatform.protocol.SentryId
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.timeIntervalSinceNow

/**
 * Wraps a Cocoa SentryLog, delegating all mutations directly to it.
 */
internal class CocoaSentryLogDelegate(private val cocoaLog: CocoaSentryLog) : SentryLog(
    timestamp = cocoaLog.timestamp().timeIntervalSince1970,
    level = cocoaLog.level().toKmpSentryLogLevel(),
    body = cocoaLog.body(),
    ) {

    override var body: String
        get() = cocoaLog.body()
        set(value) { cocoaLog.setBody(value) }

    override var level: SentryLogLevel
        get() = cocoaLog.level().toKmpSentryLogLevel()
        set(value) { value.toCocoaSentryLogLevel().let { cocoaLog.setLevel(it) } }

    override var severityNumber: Int?
        get() = cocoaLog.severityNumber()?.intValue
        set(value) { cocoaLog.setSeverityNumber(value?.toLong()) }

    override val attributes: KmpSentryAttributes = CocoaSentryAttributesDelegate(cocoaLog)
}

/**
 * Wraps Cocoa log attributes, delegating mutations directly.
 */
private class CocoaSentryAttributesDelegate(private val cocoaLog: CocoaSentryLog) : KmpSentryAttributes() {
    override fun setAttribute(attribute: KmpSentryAttribute) = setAttributesFromCollection(listOf(attribute))

    override fun setAttributes(attributes: List<KmpSentryAttribute>) = setAttributesFromCollection(attributes)

    override fun setAttributes(attributes: Map<String, KmpSentryAttribute>) = setAttributesFromCollection(attributes.values)

    override fun removeAttribute(key: String) {
        val mutableAttributes = cocoaLog.attributes().toMutableMap()
        mutableAttributes.remove(key)
        cocoaLog.setAttributes(mutableAttributes)
    }

    private fun setAttributesFromCollection(attributes: Collection<KmpSentryAttribute>) {
        val mutableAttributes = cocoaLog.attributes().toMutableMap()
        for (attribute in attributes) {
            mutableAttributes[attribute.key] = attribute.value
        }
        cocoaLog.setAttributes(mutableAttributes)
    }
}

/**
 * Returns a [SentryLog] delegate that forwards all mutations to this Cocoa log.
 */
internal fun CocoaSentryLog.asSentryLogDelegate(): SentryLog = CocoaSentryLogDelegate(this)

/**
 * Converts Cocoa SDK's [CocoaSentryLogLevel] to KMP [SentryLogLevel].
 */
internal fun CocoaSentryLogLevel.toKmpSentryLogLevel(): SentryLogLevel = when (this) {
    0 -> SentryLogLevel.TRACE
    1 -> SentryLogLevel.DEBUG
    2 -> SentryLogLevel.INFO
    3 -> SentryLogLevel.WARN
    4 -> SentryLogLevel.ERROR
    5 -> SentryLogLevel.FATAL
    else -> SentryLogLevel.DEBUG
}

/**
 * Converts KMP's [SentryLogLevel] to Cocoa SDK's [CocoaSentryLogLevel].
 */
internal fun SentryLogLevel.toCocoaSentryLogLevel(): CocoaSentryLogLevel = when (this) {
    SentryLogLevel.TRACE -> 0
    SentryLogLevel.DEBUG -> 1
    SentryLogLevel.INFO -> 2
    SentryLogLevel.WARN -> 3
    SentryLogLevel.ERROR -> 4
    SentryLogLevel.FATAL -> 5
}

