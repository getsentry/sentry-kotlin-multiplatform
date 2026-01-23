package io.sentry.kotlin.multiplatform.log

import kotlinx.cinterop.convert
import platform.Foundation.NSNumber
import cocoapods.Sentry.SentryLogLevel as CocoaSentryLogLevel
import cocoapods.Sentry.SentryLog as CocoaSentryLog
import io.sentry.kotlin.multiplatform.SentryAttributes as KmpSentryAttributes
import io.sentry.kotlin.multiplatform.SentryAttributeValue as KmpSentryAttribute
import platform.Foundation.timeIntervalSince1970

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
        set(value) {
            value?.let { cocoaLog.setSeverityNumber(NSNumber(int = it)) }
        }

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
internal fun CocoaSentryLogLevel.toKmpSentryLogLevel(): SentryLogLevel = when (this.convert<Int>()) {
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
    SentryLogLevel.TRACE -> 0.convert()
    SentryLogLevel.DEBUG -> 1.convert()
    SentryLogLevel.INFO -> 2.convert()
    SentryLogLevel.WARN -> 3.convert()
    SentryLogLevel.ERROR -> 4.convert()
    SentryLogLevel.FATAL -> 5.convert()
}

