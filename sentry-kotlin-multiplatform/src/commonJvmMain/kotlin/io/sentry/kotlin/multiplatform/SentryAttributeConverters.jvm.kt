package io.sentry.kotlin.multiplatform

import io.sentry.SentryLogEventAttributeValue

/** Converts shared attributes for native logging and metrics calls. */
internal fun SentryAttributes.toJvmSentryAttributes(): JvmSentryAttributes =
    JvmSentryAttributes.fromMap(
        mapValues { it.value.value },
    )

/**
 * Converts native scalar attributes to KMP SentryAttributes.
 */
internal fun Map<String, SentryLogEventAttributeValue?>?.toKmpSentryAttributes(): SentryAttributes {
    val kmpAttributes = SentryAttributes.empty()
    this?.forEach { (key, value) ->
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

/** Applies scalar edits while retaining native attributes that KMP cannot represent. */
internal fun updateJvmAttributes(
    modified: SentryAttributes,
    originalKeys: Set<String>,
    remove: (String) -> Unit,
    set: (String, SentryLogEventAttributeValue) -> Unit,
) {
    (originalKeys - modified.keys).forEach(remove)
    modified.forEach { (key, attribute) ->
        val type =
            when (attribute) {
                is SentryAttributeValue.StringValue -> JvmSentryAttributeType.STRING
                is SentryAttributeValue.LongValue -> JvmSentryAttributeType.INTEGER
                is SentryAttributeValue.DoubleValue -> JvmSentryAttributeType.DOUBLE
                is SentryAttributeValue.BooleanValue -> JvmSentryAttributeType.BOOLEAN
            }
        set(key, SentryLogEventAttributeValue(type, attribute.value))
    }
}
