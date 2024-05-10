package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.SentryEvent
import io.sentry.kotlin.multiplatform.SentryOptions



internal fun SentryOptions.toJvmSentryOptionsCallback(): (JvmSentryOptions) -> Unit = {
    it.applyJvmBaseOptions(this)
    it.configureSdkNameAndPackages()
}

internal fun JvmSentryOptions.configureSdkNameAndPackages() {
    // Apply SDK version details
    sdkVersion?.name = sdkVersion?.name ?: BuildKonfig.SENTRY_KMP_JAVA_SDK_NAME
    sdkVersion?.version = sdkVersion?.version ?: BuildKonfig.VERSION_NAME

    // Add packages from the source if they are not already present
    sdkVersion?.packages?.forEach { sdkPackage ->
        if (sdkVersion?.packages?.none { it.name == sdkPackage.name } == true) {
            sdkVersion?.addPackage(sdkPackage.name, sdkPackage.version)
        }
    }

    // Ensure the main Java SDK package is included
    if (sdkVersion?.packages?.none { it.name == BuildKonfig.SENTRY_JAVA_PACKAGE_NAME } == true) {
        sdkVersion?.addPackage(
            BuildKonfig.SENTRY_JAVA_PACKAGE_NAME,
            BuildKonfig.SENTRY_JAVA_VERSION
        )
    }
}


/**
 * Applies the given base SentryOptions to this JvmSentryOption
 * This avoids code duplication during init on Android
 */
internal fun JvmSentryOptions.applyJvmBaseOptions(options: SentryOptions) {
    dsn = options.dsn
    isAttachThreads = options.attachThreads
    isAttachStacktrace = options.attachStackTrace
    dist = options.dist
    environment = options.environment
    release = options.release
    isDebug = options.debug
    sessionTrackingIntervalMillis = options.sessionTrackingIntervalMillis
    isEnableAutoSessionTracking = options.enableAutoSessionTracking
    maxAttachmentSize = options.maxAttachmentSize
    maxBreadcrumbs = options.maxBreadcrumbs
    sampleRate = options.sampleRate
    tracesSampleRate = options.tracesSampleRate
    setBeforeBreadcrumb { jvmBreadcrumb, _ ->
        if (options.beforeBreadcrumb == null) {
            jvmBreadcrumb
        } else {
            options.beforeBreadcrumb?.invoke(jvmBreadcrumb.toKmpBreadcrumb())?.toJvmBreadcrumb()
        }
    }
    setBeforeSend { jvmSentryEvent, hint ->
        if (options.beforeSend == null) {
            jvmSentryEvent
        } else {
            options.beforeSend?.invoke(SentryEvent(jvmSentryEvent))?.let {
                jvmSentryEvent.applyKmpEvent(it)
            }
        }
    }
}
