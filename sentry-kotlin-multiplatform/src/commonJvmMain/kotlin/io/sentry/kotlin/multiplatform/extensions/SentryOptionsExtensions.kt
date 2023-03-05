package io.sentry.kotlin.multiplatform.extensions

import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.JvmSentryOptions
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.protocol.SdkVersion
import io.sentry.kotlin.multiplatform.protocol.addPackage

internal fun SentryOptions.toJvmSentryOptionsCallback(): (JvmSentryOptions) -> Unit = {
    it.applyJvmBaseOptions(this)
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
    this.setBeforeSend { event, _ ->
        val jvmName = BuildKonfig.SENTRY_JVM_SDK_NAME
        val jvmVersion = BuildKonfig.SENTRY_JVM_VERSION

        val defaultSdk = SdkVersion().apply {
            addPackage(jvmName, jvmVersion)
        }.toJvmSdkVersion()

        event.sdk = options.sdk?.toJvmSdkVersion() ?: defaultSdk
        event
    }
    this.setBeforeBreadcrumb { jvmBreadcrumb, _ ->
        jvmBreadcrumb
            .toKmpBreadcrumb()
            .apply { options.beforeBreadcrumb?.invoke(this) }
            .toJvmBreadcrumb()
    }
}
