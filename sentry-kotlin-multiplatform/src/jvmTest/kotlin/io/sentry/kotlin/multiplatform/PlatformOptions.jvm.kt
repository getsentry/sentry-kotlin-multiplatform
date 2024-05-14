package io.sentry.kotlin.multiplatform

import io.sentry.kotlin.multiplatform.extensions.toJvmSentryOptionsCallback
import io.sentry.kotlin.multiplatform.utils.fakeDsn
import kotlin.test.assertEquals

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

actual class SentryPlatformOptionsFoo {
    private var event: JvmSentryEvent? = null

    actual fun init() {
        Sentry.initWithPlatformOptions {
            it.dsn = fakeDsn
            it.setBeforeSend { event, _ ->
                this.event = event
                event
            }
        }

        // Trigger beforeSend
        Sentry.captureMessage("Test Message")
    }

    actual fun assertSdkNameAndVersion() {
        val sdk = event?.sdk
        assertEquals(sdk!!.name, BuildKonfig.SENTRY_KMP_JAVA_SDK_NAME)
        assertEquals(sdk.version, BuildKonfig.VERSION_NAME)
    }
}