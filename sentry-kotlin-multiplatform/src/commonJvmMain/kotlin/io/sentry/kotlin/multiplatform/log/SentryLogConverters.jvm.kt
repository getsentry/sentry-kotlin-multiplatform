package io.sentry.kotlin.multiplatform.log

import io.sentry.kotlin.multiplatform.JvmSentryLog
import io.sentry.kotlin.multiplatform.JvmSentryLogLevel
import io.sentry.kotlin.multiplatform.toKmpSentryAttributes
import io.sentry.kotlin.multiplatform.updateJvmAttributes
import io.sentry.kotlin.multiplatform.SentryAttributes as KmpSentryAttributes

/**
 * Converts KMP [SentryLogLevel] to Java SDK's [JvmSentryLogLevel].
 */
internal fun SentryLogLevel.toJvmSentryLogLevel(): JvmSentryLogLevel =
    when (this) {
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
internal fun JvmSentryLogLevel.toKmpSentryLogLevel(): SentryLogLevel =
    when (this) {
        JvmSentryLogLevel.TRACE -> SentryLogLevel.TRACE
        JvmSentryLogLevel.DEBUG -> SentryLogLevel.DEBUG
        JvmSentryLogLevel.INFO -> SentryLogLevel.INFO
        JvmSentryLogLevel.WARN -> SentryLogLevel.WARN
        JvmSentryLogLevel.ERROR -> SentryLogLevel.ERROR
        JvmSentryLogLevel.FATAL -> SentryLogLevel.FATAL
    }

/**
 * Converts a JVM SentryLog to a KMP SentryLog for use in beforeSendLog callback.
 * After the callback, changes are applied back via [updateFrom].
 */
internal fun JvmSentryLog.toKmpSentryLog(): SentryLog =
    SentryLog(
        timestamp = timestamp,
        level = level.toKmpSentryLogLevel(),
        body = body,
        severityNumber = severityNumber,
        attributes = attributes.toKmpSentryAttributes(),
    )

/**
 * Updates this JVM SentryLog from a KMP SentryLog.
 * @param kmpLog The modified KMP log after user's beforeSendLog callback.
 * @param originalKmpAttributes The original KMP attributes before the callback, used to detect changes.
 */
internal fun JvmSentryLog.updateFrom(
    kmpLog: SentryLog,
    originalKmpAttributes: KmpSentryAttributes,
) {
    body = kmpLog.body
    level = kmpLog.level.toJvmSentryLogLevel()
    severityNumber = kmpLog.severityNumber
    updateJvmAttributes(kmpLog.attributes, originalKmpAttributes.keys, { attributes?.remove(it) }, ::setAttribute)
}
