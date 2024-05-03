package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.SentryEvent
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toJvmSentryOptionsCallback(): (JvmSentryOptions) -> Unit = {
    it.applyJvmBaseOptions(this)

    // Apply JVM specific options
    it.sdkVersion?.name = sdk?.name ?: BuildKonfig.SENTRY_KMP_JAVA_SDK_NAME
    it.sdkVersion?.version = sdk?.version ?: BuildKonfig.VERSION_NAME

    sdk?.packages?.forEach { sdkPackage ->
        it.sdkVersion?.addPackage(sdkPackage.name, sdkPackage.version)
    }

    if (it.sdkVersion?.packages?.none { it.name == BuildKonfig.SENTRY_JAVA_PACKAGE_NAME } == true) {
        it.sdkVersion?.addPackage(
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
    isEnableSpotlight = options.enableSpotlight
    println("Spotlight Enabled: ${options.enableSpotlight}")
    options.spotlightUrl?.let {
        spotlightConnectionUrl = it
    }
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
