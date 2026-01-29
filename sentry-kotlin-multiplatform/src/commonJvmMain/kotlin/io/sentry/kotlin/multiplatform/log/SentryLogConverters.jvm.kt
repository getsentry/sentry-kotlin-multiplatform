package io.sentry.kotlin.multiplatform.log

import io.sentry.SentryLogEventAttributeValue
import io.sentry.kotlin.multiplatform.JvmSentryAttributeType
import io.sentry.kotlin.multiplatform.JvmSentryAttributes
import io.sentry.kotlin.multiplatform.JvmSentryLog
import io.sentry.kotlin.multiplatform.JvmSentryLogLevel
import io.sentry.kotlin.multiplatform.SentryAttributeValue as KmpSentryAttributeValue
import io.sentry.kotlin.multiplatform.SentryAttributes as KmpSentryAttributes

/**
 * Converts KMP [SentryLogLevel] to Java SDK's [JvmSentryLogLevel].
 */
internal fun SentryLogLevel.toJvmSentryLogLevel(): JvmSentryLogLevel = when (this) {
    SentryLogLevel.TRACE -> JvmSentryLogLevel.TRACE
    SentryLogLevel.DEBUG -> JvmSentryLogLevel.DEBUG
    SentryLogLevel.INFO -> JvmSentryLogLevel.INFO
    SentryLogLevel.WARN -> JvmSentryLogLevel.WARN
    SentryLogLevel.ERROR -> JvmSentryLogLevel.ERROR
    SentryLogLevel.FATAL -> JvmSentryLogLevel.FATAL
}

/**
 * Converts Java SDK's [JvmSentryLogLevel] to KMP [SentryLogLevel].
 */
internal fun JvmSentryLogLevel.toKmpSentryLogLevel(): SentryLogLevel = when (this) {
    JvmSentryLogLevel.TRACE -> SentryLogLevel.TRACE
    JvmSentryLogLevel.DEBUG -> SentryLogLevel.DEBUG
    JvmSentryLogLevel.INFO -> SentryLogLevel.INFO
    JvmSentryLogLevel.WARN -> SentryLogLevel.WARN
    JvmSentryLogLevel.ERROR -> SentryLogLevel.ERROR
    JvmSentryLogLevel.FATAL -> SentryLogLevel.FATAL
}

/**
 * Converts KMP [KmpSentryAttributes] to Java SDK's [JvmSentryAttributes].
 * * This is needed for the Java SDK's SentryLogParameters.create method.
 */
internal fun KmpSentryAttributes.toJvmSentryAttributes(): JvmSentryAttributes {
    val map = mutableMapOf<String, Any>()
    forEach { (key, attrValue) ->
        map[key] = attrValue.value
    }
    return JvmSentryAttributes.fromMap(map)
}

/**
 * Converts a JVM SentryLog to a KMP SentryLog for use in beforeSendLog callback.
 * After the callback, changes are applied back via [updateFrom].
 */
internal fun JvmSentryLog.toKmpSentryLog(): SentryLog = SentryLog(
    timestamp = timestamp,
    level = level.toKmpSentryLogLevel(),
    body = body,
    severityNumber = severityNumber,
    attributes = toKmpSentryAttributes()
)

/**
 * Updates this JVM SentryLog from a KMP SentryLog.
 * @param kmpLog The modified KMP log after user's beforeSendLog callback.
 * @param originalKmpAttributes The original KMP attributes before the callback, used to detect changes.
 */
internal fun JvmSentryLog.updateFrom(kmpLog: SentryLog, originalKmpAttributes: KmpSentryAttributes) {
    body = kmpLog.body
    level = kmpLog.level.toJvmSentryLogLevel()
    severityNumber = kmpLog.severityNumber
    updateAttributesFrom(kmpLog.attributes, originalKmpAttributes)
}

/**
 * Converts JVM log attributes to KMP SentryAttributes.
 */
private fun JvmSentryLog.toKmpSentryAttributes(): KmpSentryAttributes {
    val kmpAttributes = KmpSentryAttributes.empty()
    attributes?.forEach { (key, value) ->
        val attrValue = value ?: return@forEach
        when (attrValue.type) {
            JvmSentryAttributeType.STRING.apiName() -> kmpAttributes[key] = attrValue.value as String
            JvmSentryAttributeType.INTEGER.apiName() -> kmpAttributes[key] = (attrValue.value as Number).toLong()
            JvmSentryAttributeType.DOUBLE.apiName() -> kmpAttributes[key] = (attrValue.value as Number).toDouble()
            JvmSentryAttributeType.BOOLEAN.apiName() -> kmpAttributes[key] = attrValue.value as Boolean
        }
    }
    return kmpAttributes
}

/**
 * Updates this JVM log's attributes from KMP attributes, merging with existing attributes.
 * Only applies changes (additions, modifications, deletions) made by the user in beforeSendLog.
 * Preserves original JVM attributes that weren't convertible to KMP or weren't modified.
 *
 * @param modifiedKmpAttributes The KMP attributes after the user's beforeSendLog callback.
 * @param originalKmpAttributes The KMP attributes before the callback, used to detect changes.
 */
private fun JvmSentryLog.updateAttributesFrom(
    modifiedKmpAttributes: KmpSentryAttributes,
    originalKmpAttributes: KmpSentryAttributes
) {
    // Collect keys from both original and modified KMP attributes
    val originalKeys = mutableSetOf<String>()
    originalKmpAttributes.forEach { (key, _) -> originalKeys.add(key) }

    val modifiedKeys = mutableSetOf<String>()
    modifiedKmpAttributes.forEach { (key, _) -> modifiedKeys.add(key) }

    // Remove attributes that were deleted by the user (present in original but not in modified)
    val deletedKeys = originalKeys - modifiedKeys
    deletedKeys.forEach { key ->
        attributes?.remove(key)
    }

    // Add or update attributes from the modified KMP attributes
    modifiedKmpAttributes.forEach { (key, attrValue) ->
        val jvmType = when (attrValue) {
            is KmpSentryAttributeValue.StringValue -> JvmSentryAttributeType.STRING
            is KmpSentryAttributeValue.LongValue -> JvmSentryAttributeType.INTEGER
            is KmpSentryAttributeValue.DoubleValue -> JvmSentryAttributeType.DOUBLE
            is KmpSentryAttributeValue.BooleanValue -> JvmSentryAttributeType.BOOLEAN
        }
        setAttribute(key, SentryLogEventAttributeValue(jvmType, attrValue.value))
    }
}
