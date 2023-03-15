package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toJvmSentryOptionsCallback(): (JvmSentryOptions) -> Unit = {
    it.applyJvmBaseOptions(this)

    // Apply JVM specific options
    it.sdkVersion?.name = this.sdk?.name ?: BuildKonfig.SENTRY_KMP_JAVA_SDK_NAME
    it.sdkVersion?.version = this.sdk?.version ?: BuildKonfig.VERSION_NAME

    this.sdk?.packages?.forEach { sdkPackage ->
        it.sdkVersion?.addPackage(sdkPackage.name, sdkPackage.version)
    }

    if (it.sdkVersion?.packages?.none { it.name == BuildKonfig.SENTRY_JAVA_PACKAGE_NAME } == true) {
        it.sdkVersion?.addPackage(BuildKonfig.SENTRY_JAVA_PACKAGE_NAME, BuildKonfig.SENTRY_JAVA_VERSION)
    }
}

/**
 * Applies the given base SentryOptions to this JvmSentryOption
 * This avoids code duplication during init on Android
 */
internal fun JvmSentryOptions.applyJvmBaseOptions(options: SentryOptions) {
    this.dsn = options.dsn
    this.isAttachThreads = options.attachThreads
    this.isAttachStacktrace = options.attachStackTrace
    this.dist = options.dist
    this.environment = options.environment
    this.release = options.release
    this.isDebug = options.debug
    this.sessionTrackingIntervalMillis = options.sessionTrackingIntervalMillis
    this.isEnableAutoSessionTracking = options.enableAutoSessionTracking
    this.maxAttachmentSize = options.maxAttachmentSize
    this.maxBreadcrumbs = options.maxBreadcrumbs
    this.setBeforeBreadcrumb { jvmBreadcrumb, _ ->
        options.beforeBreadcrumb?.invoke(jvmBreadcrumb.toKmpBreadcrumb())?.toJvmBreadcrumb()
    }
}
