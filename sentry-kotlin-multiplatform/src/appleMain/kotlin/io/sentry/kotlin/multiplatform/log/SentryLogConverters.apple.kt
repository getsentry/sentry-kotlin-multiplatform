package io.sentry.kotlin.multiplatform.log

import cocoapods.Sentry.SentryLog as CocoaSentryLog
import cocoapods.Sentry.SentryLogLevel as CocoaSentryLogLevel
import io.sentry.kotlin.multiplatform.SentryAttributes as KmpSentryAttributes
import kotlinx.cinterop.convert
import platform.Foundation.NSNumber
import platform.Foundation.timeIntervalSince1970

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
    kmpLog.severityNumber?.let { setSeverityNumber(NSNumber(int = it)) }
    updateAttributesFrom(kmpLog.attributes, originalKmpAttributes)
}

/**
 * Converts Cocoa log attributes to KMP SentryAttributes.
 */
private fun CocoaSentryLog.toKmpSentryAttributes(): KmpSentryAttributes {
    val kmpAttributes = KmpSentryAttributes.empty()
    attributes().forEach { (key, value) ->
        val keyStr = key as? String ?: return@forEach
        when (value) {
            is String -> kmpAttributes[keyStr] = value
            is Boolean -> kmpAttributes[keyStr] = value
            is NSNumber -> {
                // NSNumber could be integer or floating point
                val doubleValue = value.doubleValue
                if (doubleValue == doubleValue.toLong().toDouble()) {
                    // It's an integer type - store as Long
                    kmpAttributes[keyStr] = value.longValue
                } else {
                    kmpAttributes[keyStr] = doubleValue
                }
            }
            is Long -> kmpAttributes[keyStr] = value
            is Double -> kmpAttributes[keyStr] = value
        }
    }
    return kmpAttributes
}

/**
 * Updates this Cocoa log's attributes from KMP attributes, merging with existing attributes.
 * Only applies changes (additions, modifications, deletions) made by the user in beforeSendLog.
 * Preserves original Cocoa attributes that weren't convertible to KMP or weren't modified.
 *
 * @param modifiedKmpAttributes The KMP attributes after the user's beforeSendLog callback.
 * @param originalKmpAttributes The KMP attributes before the callback, used to detect changes.
 */
private fun CocoaSentryLog.updateAttributesFrom(
    modifiedKmpAttributes: KmpSentryAttributes,
    originalKmpAttributes: KmpSentryAttributes
) {
    // Start with the current Cocoa attributes (preserves unconvertible types)
    val mergedAttributes = attributes().toMutableMap()

    // Collect keys from both original and modified KMP attributes
    val originalKeys = mutableSetOf<String>()
    originalKmpAttributes.forEach { (key, _) -> originalKeys.add(key) }

    val modifiedKeys = mutableSetOf<String>()
    modifiedKmpAttributes.forEach { (key, _) -> modifiedKeys.add(key) }

    // Remove attributes that were deleted by the user (present in original but not in modified)
    val deletedKeys = originalKeys - modifiedKeys
    deletedKeys.forEach { key ->
        mergedAttributes.remove(key)
    }

    // Add or update attributes from the modified KMP attributes
    modifiedKmpAttributes.forEach { (key, attrValue) ->
        mergedAttributes[key] = attrValue.value
    }

    setAttributes(mergedAttributes)
}
