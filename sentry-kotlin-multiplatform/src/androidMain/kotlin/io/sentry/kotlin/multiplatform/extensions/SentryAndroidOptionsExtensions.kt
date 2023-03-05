package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.protocol.SdkVersion
import io.sentry.kotlin.multiplatform.protocol.addPackage

internal fun SentryOptions.toAndroidSentryOptionsCallback(): (SentryAndroidOptions) -> Unit = {
    // Apply base options available to all JVM targets
    it.applyJvmBaseOptions(this)

    // Apply Android specific options
    it.isAttachScreenshot = this.attachScreenshot
    it.setBeforeSend { event, _ ->
        val androidName = BuildKonfig.SENTRY_ANDROID_SDK_NAME
        val androidVersion = BuildKonfig.SENTRY_ANDROID_VERSION

        val defaultSdk = SdkVersion().apply {
            addPackage(androidName, androidVersion)
        }.toJvmSdkVersion()

        event.sdk = this.sdk?.toJvmSdkVersion() ?: defaultSdk
        event
    }
}
