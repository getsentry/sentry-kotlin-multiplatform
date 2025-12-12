package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.SentryEvent
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toJvmSentryOptionsCallback(): (JvmSentryOptions) -> Unit = {
    it.applyJvmBaseOptions(this)

    sdk?.packages?.forEach { sdkPackage ->
        it.sdkVersion?.addPackage(sdkPackage.name, sdkPackage.version)
    }
}

/**
 * Applies the given base SentryOptions to this JvmSentryOption
 * This avoids code duplication during init on Android
 */
internal fun JvmSentryOptions.applyJvmBaseOptions(kmpOptions: SentryOptions) {
    val jvmOptions = this
    jvmOptions.dsn = kmpOptions.dsn
    jvmOptions.isAttachThreads = kmpOptions.attachThreads
    jvmOptions.isAttachStacktrace = kmpOptions.attachStackTrace
    jvmOptions.dist = kmpOptions.dist
    jvmOptions.environment = kmpOptions.environment
    jvmOptions.isSendDefaultPii = kmpOptions.sendDefaultPii
    jvmOptions.release = kmpOptions.release
    jvmOptions.isDebug = kmpOptions.debug
    jvmOptions.sessionTrackingIntervalMillis = kmpOptions.sessionTrackingIntervalMillis
    jvmOptions.isEnableAutoSessionTracking = kmpOptions.enableAutoSessionTracking
    jvmOptions.maxAttachmentSize = kmpOptions.maxAttachmentSize
    jvmOptions.maxBreadcrumbs = kmpOptions.maxBreadcrumbs
    jvmOptions.sampleRate = kmpOptions.sampleRate
    jvmOptions.tracesSampleRate = kmpOptions.tracesSampleRate
    jvmOptions.setDiagnosticLevel(kmpOptions.diagnosticLevel.toJvmSentryLevel())
    jvmOptions.logs.isEnabled = kmpOptions.enableLogs
    jvmOptions.setBeforeBreadcrumb { jvmBreadcrumb, _ ->
        if (kmpOptions.beforeBreadcrumb == null) {
            jvmBreadcrumb
        } else {
            kmpOptions.beforeBreadcrumb?.invoke(jvmBreadcrumb.toKmpBreadcrumb())?.toJvmBreadcrumb()
        }
    }
    jvmOptions.setBeforeSend { jvmSentryEvent, hint ->
        if (kmpOptions.beforeSend == null) {
            jvmSentryEvent
        } else {
            kmpOptions.beforeSend?.invoke(SentryEvent(jvmSentryEvent))?.let {
                jvmSentryEvent.applyKmpEvent(it)
            }
        }
    }
}
