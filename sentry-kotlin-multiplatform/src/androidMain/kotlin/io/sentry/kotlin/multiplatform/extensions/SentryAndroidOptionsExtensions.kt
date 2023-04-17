package io.sentry.kotlin.multiplatform.extensions

import io.sentry.android.core.SentryAndroidOptions
import io.sentry.kotlin.multiplatform.BuildKonfig
import io.sentry.kotlin.multiplatform.SentryOptions

internal fun SentryOptions.toAndroidSentryOptionsCallback(): (SentryAndroidOptions) -> Unit = {
    // Apply base options available to all JVM targets
    it.applyJvmBaseOptions(this)

    // Apply Android specific options
    it.isAttachScreenshot = this.attachScreenshot
    it.isAttachViewHierarchy = this.attachViewHierarchy

    it.sdkVersion?.name = this.sdk?.name ?: BuildKonfig.SENTRY_KMP_ANDROID_SDK_NAME
    it.sdkVersion?.version = this.sdk?.version ?: BuildKonfig.VERSION_NAME

    this.sdk?.packages?.forEach { sdkPackage ->
        it.sdkVersion?.addPackage(sdkPackage.name, sdkPackage.version)
    }

    if (it.sdkVersion?.packages?.none { it.name == BuildKonfig.SENTRY_ANDROID_PACKAGE_NAME } == true) {
        it.sdkVersion?.addPackage(BuildKonfig.SENTRY_ANDROID_PACKAGE_NAME, BuildKonfig.SENTRY_ANDROID_VERSION)
    }
}
