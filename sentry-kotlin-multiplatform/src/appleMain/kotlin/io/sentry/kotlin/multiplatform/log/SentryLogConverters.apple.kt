package io.sentry.kotlin.multiplatform.log

import cocoapods.Sentry.SentryStructuredLogAttribute
import io.sentry.kotlin.multiplatform.SentryAttributeValue
import kotlinx.cinterop.convert
import platform.Foundation.NSNumber
import platform.Foundation.timeIntervalSince1970
import cocoapods.Sentry.SentryLog as CocoaSentryLog
import cocoapods.Sentry.SentryLogLevel as CocoaSentryLogLevel
import io.sentry.kotlin.multiplatform.SentryAttributes as KmpSentryAttributes

/**
 * Converts Cocoa SDK's [CocoaSentryLogLevel] to KMP [SentryLogLevel].
 */
@Suppress("MagicNumber")
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
@Suppress("MagicNumber")
internal fun SentryLogLevel.toCocoaSentryLogLevel(): CocoaSentryLogLevel = when (this) {
    SentryLogLevel.TRACE -> 0.convert()
    SentryLogLevel.DEBUG -> 1.convert()
    SentryLogLevel.INFO -> 2.convert()
    SentryLogLevel.WARN -> 3.convert()
    SentryLogLevel.ERROR -> 4.convert()
    SentryLogLevel.FATAL -> 5.convert()
}

/**
 * Converts a Cocoa SentryLog to a KMP SentryLog for use in beforeSendLog callback.
 * After the callback, changes are applied back via [updateFrom].
 */
internal fun CocoaSentryLog.toKmpSentryLog(): SentryLog = SentryLog(
    timestamp = timestamp().timeIntervalSince1970,
    level = level().toKmpSentryLogLevel(),
    body = body(),
    severityNumber = severityNumber()?.intValue,
    attributes = toKmpSentryAttributes()
)

/**
 * Updates this Cocoa SentryLog from a KMP SentryLog.
 * @param kmpLog The modified KMP log after user's beforeSendLog callback.
 * @param originalKmpAttributes The original KMP attributes before the callback, used to detect changes.
 */
internal fun CocoaSentryLog.updateFrom(kmpLog: SentryLog, originalKmpAttributes: KmpSentryAttributes) {
    setBody(kmpLog.body)
    setLevel(kmpLog.level.toCocoaSentryLogLevel())
    setSeverityNumber(kmpLog.severityNumber?.let { NSNumber(int = it) })
    updateAttributesFrom(kmpLog.attributes, originalKmpAttributes)
}

/**
 * Converts Cocoa log attributes to KMP SentryAttributes.
 */
private fun CocoaSentryLog.toKmpSentryAttributes(): KmpSentryAttributes {
    val kmpAttributes = KmpSentryAttributes.empty()
    attributes()
        .mapNotNull { (key, value) ->
            (key as? String)?.let { it to (value as? SentryStructuredLogAttribute) }
        }
        .forEach { (key, attribute) ->
            attribute ?: return@forEach
            when (attribute.type()) {
                "string" -> kmpAttributes[key] = attribute.value() as String
                "boolean" -> kmpAttributes[key] = (attribute.value() as NSNumber).boolValue
                "integer" -> kmpAttributes[key] = (attribute.value() as NSNumber).longValue
                "double" -> kmpAttributes[key] = (attribute.value() as NSNumber).doubleValue
            }
        }
    return kmpAttributes
}

/**
 * Applies attribute changes from the beforeSendLog callback back to this Cocoa log.
 */
private fun CocoaSentryLog.updateAttributesFrom(
    modifiedKmpAttributes: KmpSentryAttributes,
    originalKmpAttributes: KmpSentryAttributes
) {
    val mergedAttributes = attributes().toMutableMap()

    // Remove attributes deleted by the user (present in original but not in modified)
    (originalKmpAttributes.keys - modifiedKmpAttributes.keys).forEach { mergedAttributes.remove(it) }

    modifiedKmpAttributes.forEach { (key, attrValue) ->
        mergedAttributes[key] = when (attrValue) {
            is SentryAttributeValue.LongValue -> SentryStructuredLogAttribute(integer = (attrValue.value as Long).convert())
            is SentryAttributeValue.DoubleValue -> SentryStructuredLogAttribute(double = attrValue.value as Double)
            is SentryAttributeValue.StringValue -> SentryStructuredLogAttribute(string = attrValue.value as String)
            is SentryAttributeValue.BooleanValue -> SentryStructuredLogAttribute(boolean = attrValue.value as Boolean)
        }
    }

    setAttributes(mergedAttributes)
}
