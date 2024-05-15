package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toJvmSentryOptionsCallback
import io.sentry.kotlin.multiplatform.protocol.SdkVersion
import io.sentry.kotlin.multiplatform.utils.fakeDsn

actual interface PlatformOptions : CommonPlatformOptions

class SentryJvmOptionsWrapper(private val jvmOptions: JvmSentryOptions) : PlatformOptions {
    override val dsn: String?
        get() = jvmOptions.dsn

    override val attachStackTrace: Boolean
        get() = jvmOptions.isAttachStacktrace

    override val release: String?
        get() = jvmOptions.release

    override val debug: Boolean
        get() = jvmOptions.isDebug

    override val environment: String?
        get() = jvmOptions.environment

    override val dist: String?
        get() = jvmOptions.dist

    override val enableAutoSessionTracking: Boolean
        get() = jvmOptions.isEnableAutoSessionTracking

    override val sessionTrackingIntervalMillis: Long
        get() = jvmOptions.sessionTrackingIntervalMillis

    override val maxBreadcrumbs: Int
        get() = jvmOptions.maxBreadcrumbs

    override val maxAttachmentSize: Long
        get() = jvmOptions.maxAttachmentSize

    override val sampleRate: Double?
        get() = jvmOptions.sampleRate

    override val tracesSampleRate: Double?
        get() = jvmOptions.tracesSampleRate

    override fun applyFromOptions(options: SentryOptions) {
        options.toJvmSentryOptionsCallback().invoke(jvmOptions)
    }
}

actual fun createPlatformOptions(): PlatformOptions = SentryJvmOptionsWrapper(JvmSentryOptions())

actual fun PlatformOptions.assertPlatformSpecificOptions(options: SentryOptions) {
    // no-op
}

actual fun createSentryPlatformOptionsConfiguration(): PlatformOptionsConfiguration = {
    it.dsn = fakeDsn
}

actual fun SentryPlatformOptions.toSentryOptions(): SentryOptions {
    val jvm = this@toSentryOptions
    return SentryOptions().apply {
        dsn = jvm.dsn
        release = jvm.release
        sdk = SdkVersion(jvm.sdkVersion!!.name, jvm.sdkVersion!!.version)
    }
}
