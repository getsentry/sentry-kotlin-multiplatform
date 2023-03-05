package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toJvmSentryOptionsCallback(): (JvmSentryOptions) -> Unit = {
    it.applyJvmBaseOptions(this)

    // Apply JVM specific options
    it.sdkVersion = this.sdk.toJvmSdkVersion()
    it.sdkVersion?.addPackage(
        BuildKonfig.SENTRY_JVM_SDK_NAME,
        BuildKonfig.SENTRY_JVM_VERSION
    )
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
    this.setBeforeBreadcrumb { jvmBreadcrumb, _ ->
        jvmBreadcrumb
            .toKmpBreadcrumb()
            .apply { options.beforeBreadcrumb?.invoke(this) }
            .toJvmBreadcrumb()
    }
}
