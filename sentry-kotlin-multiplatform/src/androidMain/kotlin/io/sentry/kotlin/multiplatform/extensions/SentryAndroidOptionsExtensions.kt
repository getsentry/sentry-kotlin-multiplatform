package io.sentry.kotlin.multiplatform.extensions

import android.util.Log
import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.SentryOptions
import io.sentry.kotlin.multiplatform.protocol.Package

internal fun SentryOptions.toAndroidSentryOptionsCallback(): (SentryAndroidOptions) -> Unit = {
    // Apply base options available to all JVM targets
    it.applyJvmBaseOptions(this)

    // Apply Android specific options
    it.isAttachScreenshot = this.attachScreenshot
    it.sdkVersion = sdk.toJvmSdkVersion()
    it.sdkVersion?.addPackage(
        BuildKonfig.SENTRY_ANDROID_SDK_NAME,
        BuildKonfig.SENTRY_ANDROID_VERSION
    )
}
