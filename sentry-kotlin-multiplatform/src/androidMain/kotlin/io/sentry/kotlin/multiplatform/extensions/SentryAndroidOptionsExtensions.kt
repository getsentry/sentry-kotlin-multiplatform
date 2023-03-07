package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toAndroidSentryOptionsCallback(): (SentryAndroidOptions) -> Unit = {
    // Apply base options available to all JVM targets
    it.applyJvmBaseOptions(this)

    // Apply Android specific options
    it.isAttachScreenshot = this.attachScreenshot
    sdk.addPackage(BuildKonfig.SENTRY_ANDROID_PACKAGE_NAME, BuildKonfig.SENTRY_ANDROID_VERSION)
    it.sdkVersion = sdk.toJvmSdkVersion()
}
