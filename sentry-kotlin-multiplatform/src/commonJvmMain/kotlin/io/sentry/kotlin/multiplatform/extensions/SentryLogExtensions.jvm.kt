package io.sentry.kotlin.multiplatform.extensions

import io.sentry.Sentry
import io.sentry.SentryLogEventAttributeValue
import io.sentry.kotlin.multiplatform.JvmSentryAttributeType
import io.sentry.kotlin.multiplatform.JvmSentryLog
import io.sentry.kotlin.multiplatform.JvmSentryLogLevel
import io.sentry.kotlin.multiplatform.SentryAttributes as KmpSentryAttributes
import io.sentry.kotlin.multiplatform.SentryAttribute as KmpSentryAttribute
import io.sentry.kotlin.multiplatform.log.SentryLog
import io.sentry.kotlin.multiplatform.log.SentryLogLevel

/**
 * Wraps a JVM SentryLog, delegating all mutations directly to it.
 * This avoids conversion overhead - changes go straight to the underlying JVM log.
 */
private class JvmSentryLogWrapper(private val jvmLog: JvmSentryLog) : SentryLog(
    timestamp = jvmLog.timestamp,
    level = jvmLog.level.toKmpSentryLogLevel(),
    body = jvmLog.body,
) {
    override var body: String
        get() = jvmLog.body
        set(value) { jvmLog.body = value }

    override var level: SentryLogLevel
        get() = jvmLog.level.toKmpSentryLogLevel()
        set(value) { jvmLog.level = value.toJvmSentryLogLevel() }

    override var severityNumber: Int?
        get() = jvmLog.severityNumber
        set(value) { jvmLog.severityNumber = value }

    override val attributes: KmpSentryAttributes = JvmSentryAttributesDelegate(jvmLog)
}

/**
 * Wraps JVM log attributes, delegating mutations directly.
 */
private class JvmSentryAttributesDelegate(private val jvmLog: JvmSentryLog) : KmpSentryAttributes() {
    override fun setAttribute(attribute: KmpSentryAttribute) = setAttributesFromCollection(listOf(attribute))

    override fun setAttributes(attributes: List<KmpSentryAttribute>) = setAttributesFromCollection(attributes)

    override fun setAttributes(attributes: Map<String, KmpSentryAttribute>) = setAttributesFromCollection(attributes.values)

    override fun removeAttribute(key: String) {
        jvmLog.attributes?.remove(key)
    }

    private fun setAttributesFromCollection(attributes: Collection<KmpSentryAttribute>) {
        for (attribute in attributes) {
            val jvmType = when (attribute) {
                is KmpSentryAttribute.StringAttribute -> JvmSentryAttributeType.STRING
                is KmpSentryAttribute.IntAttribute -> JvmSentryAttributeType.INTEGER
                is KmpSentryAttribute.DoubleAttribute -> JvmSentryAttributeType.DOUBLE
                is KmpSentryAttribute.BooleanAttribute -> JvmSentryAttributeType.BOOLEAN
            }
            jvmLog.setAttribute(attribute.key, SentryLogEventAttributeValue(jvmType, attribute.value))
        }
    }
}

/**
 * Returns a [SentryLog] delegate that forwards all mutations to this JVM log.
 */
internal fun JvmSentryLog.asSentryLogDelegate(): SentryLog = JvmSentryLogWrapper(this)

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
