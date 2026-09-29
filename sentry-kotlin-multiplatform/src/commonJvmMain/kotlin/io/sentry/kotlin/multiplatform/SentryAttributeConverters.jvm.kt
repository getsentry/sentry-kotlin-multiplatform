package io.sentry.kotlin.multiplatform

import io.sentry.SentryLogEventAttributeValue

/**
 * Converts KMP [SentryAttributes] to Java SDK's [JvmSentryAttributes].
 * This is needed for the Java SDK's SentryLogParameters and SentryMetricsParameters.
 */
internal fun SentryAttributes.toJvmSentryAttributes(): JvmSentryAttributes {
    val values = mapValues { it.value.value }
    return JvmSentryAttributes.fromMap(values)
}

/**
 * Converts native log or metric attributes to KMP [SentryAttributes], skipping non-scalar types.
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

/** Converts a KMP attribute value to the Java SDK's attribute value. */
internal fun SentryAttributeValue.toJvmAttributeValue(): SentryLogEventAttributeValue {
    val type =
        when (this) {
            is SentryAttributeValue.StringValue -> JvmSentryAttributeType.STRING
            is SentryAttributeValue.LongValue -> JvmSentryAttributeType.INTEGER
            is SentryAttributeValue.DoubleValue -> JvmSentryAttributeType.DOUBLE
            is SentryAttributeValue.BooleanValue -> JvmSentryAttributeType.BOOLEAN
        }
    return SentryLogEventAttributeValue(type, value)
}

/** Whether this native attribute can be represented as a KMP [SentryAttributeValue]. */
internal val SentryLogEventAttributeValue.isScalar: Boolean
    get() = type in scalarTypes

private val scalarTypes =
    listOf(
        JvmSentryAttributeType.STRING,
        JvmSentryAttributeType.INTEGER,
        JvmSentryAttributeType.DOUBLE,
        JvmSentryAttributeType.BOOLEAN,
    ).map { it.apiName() }
